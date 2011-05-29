package net.dv90.starfury.net;

import java.awt.Color;
import java.awt.Point;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.dv90.starfury.entity.Player;
import net.dv90.starfury.entity.Player.PlayerColor;
import net.dv90.starfury.entity.Player.PlayerStat;
import net.dv90.starfury.inventory.ItemStack;
import net.dv90.starfury.inventory.ItemType;
import net.dv90.starfury.inventory.PlayerInventory;
import net.dv90.starfury.logging.*;

import net.dv90.starfury.util.BitConverter;
import net.dv90.starfury.util.MathUtil;
import net.dv90.starfury.world.World;

public class Client extends Thread {

    private Server server;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private int clientId;
    private NetworkState state = NetworkState.Closed;
    private boolean authenticated = false;
    private Player player;
    private boolean[][] loadedTiles;

    public Client(Server server, Socket socket, int clientId) {
        state = NetworkState.Starting;

        this.server = server;
        this.socket = socket;
        this.clientId = clientId;

        player = new Player(this);
        authenticated = (server.usingPassword() ? false : true);

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();

            start();
        } catch (Exception e) {
            e.printStackTrace();

            state = NetworkState.Error;
        }

        Logger.log(LogLevel.INFO, "Client connected [" + toString() + "]");
    }

    public Server getServer() {
        return server;
    }

    public Integer getClientID() {
        return clientId;
    }

    public Player getPlayer() {
        return player;
    }

    public void write(byte[] data) {
        try {
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        state = NetworkState.Running;

        Packet packet;
        Protocol proto;
        byte[] header, data;
        int length, id;

        while (state == NetworkState.Running && !socket.isInputShutdown()) {
            try {
                header = new byte[4];
                int read = in.read(header);
                if (read == -1) {
                    disconnect();
                } else if (read != 4) {
                    if (socket.isInputShutdown()) {
                        break;
                    }

                    throw new Exception("Malformed packet header");
                }

                length = BitConverter.toInteger(header) - 1;

                id = in.read();
                proto = Protocol.lookup(id);

                if (proto == null) {
                    throw new Exception("Unrecognized packet with ID " + id);
                }

                packet = new Packet(proto);

                data = new byte[length];
                if (in.read(data) != length) {
                    if (socket.isClosed()) {
                        break;
                    }

                    throw new Exception("Insufficient bytes available to fill packet length");
                }

                packet.append(data);
                handlePacket(packet);
            } catch (Exception e) {
                if (state == NetworkState.Running) {
                    Logger.log(LogLevel.INFO, e.getMessage() + " [" + this.toString() + "]");

                    state = NetworkState.Error;
                }

                break;
            }
        }


        disconnect();
        Logger.log(LogLevel.INFO, "Client disconnected [" + this.toString() + "]");
    }

    public void disconnect() {
        disconnect("Connection closed by server.");
    }

    public void disconnect(String reason) {
        if (state != NetworkState.Running && state != NetworkState.Error) {
            return;
        }

        server.removeClient(this);

        state = NetworkState.Closing;

        if (!socket.isOutputShutdown()) {
            Packet packet = new Packet(Protocol.Disconnect);
            packet.append(reason.getBytes());

            write(packet.create());
        }

        try {
            socket.close();

            state = NetworkState.Closed;
        } catch (Exception e) {
            e.printStackTrace();

            state = NetworkState.Error;
        }
    }

    private void sendSection(int sectionX, int sectionY) {
        loadedTiles[sectionX][sectionY] = true;

        int toSendX = sectionX * 200;
        int toSendY = sectionY * 150;
        for (int i = toSendY; i < (toSendY + 150); i++) {
            Packet packet = new Packet(Protocol.TileSection);
            packet.append(BitConverter.toBytes((short)200));
            packet.append(BitConverter.toBytes(toSendX));
            packet.append(BitConverter.toBytes(i));
            for (int j = toSendX; j < (200 + toSendX); j++) {
                byte flag = 0;
                Tile tile = World.getWorld().getTile(j, i);
                if (tile.isActive()) {
                    flag |= TileFlags.ACTIVE;
                }
                if (tile.isLight()) {
                    flag |= TileFlags.LIGHT;
                }
                if (tile.getWall() > 0) {
                    flag |= TileFlags.WALL;
                }
                if (tile.getLiquid() > 0) {
                    flag |= TileFlags.LIQUID;
                }
                packet.append(flag);

                if (tile.isActive()) {
                    packet.append(tile.getType());
                    if (World.getWorld().isTileFrameImportant(tile.getType())) {
                        packet.append(BitConverter.toShortBytes((short) tile.getFrameX()));
                        packet.append(BitConverter.toShortBytes((short) tile.getFrameY()));
                    }
                }
                if (tile.getWall() > 0) {
                    packet.append(tile.getWall());
                }
                if (tile.getLiquid() > 0) {
                    packet.append(tile.getLiquid());
                    packet.append(tile.isLava() ? 1 : 0);
                }
            }
            getClient().write(packet.create());
        }
    }

    public void handlePacket(Packet packet) throws Exception {
        Logger.log(LogLevel.DEBUG, "Processing " + packet.getProtocol().toString() + " packet. [" + this.toString() + "]");

        Packet response = null;

        if (!authenticated && packet.getProtocol() != Protocol.ConnectRequest && packet.getProtocol() != Protocol.PasswordResponse) {
            disconnect("Illegal packet received.");

            return;
        }

        int index = 0;
        byte[] data = packet.getData();

        switch (packet.getProtocol()) {
            case ConnectRequest:
                if (!new String(data).equals(server.getClientVersion())) {
                    disconnect("Incorrect client version.");

                    break;
                }

                if (server.usingPassword()) {
                    response = new Packet(Protocol.PasswordRequest);
                } else {
                    response = new Packet(Protocol.RequestPlayerData);
                    response.append(BitConverter.toBytes(clientId));
                }

                break;

            case PlayerData:
                if ((int) data[index++] != this.clientId) {
                    disconnect("Client ID mismatch.");

                    break;
                }

                player.setHairstyle((int) data[index + 1]);
                index++;

                for (PlayerColor part : PlayerColor.values()) {
                    int r = (int) data[index];
                    r = MathUtil.clamp(r, 0, 255);

                    int g = (int) data[index + 1];
                    g = MathUtil.clamp(g, 0, 255);

                    int b = (int) data[index + 2];
                    b = MathUtil.clamp(b, 0, 255);

                    index += 3;

                    Color color = new Color(r, g, b);
                    player.setColor(part, color);
                }

                byte[] name = new byte[data.length - index];
                System.arraycopy(data, index, name, 0, name.length);

                player.setName(new String(name));
                break;
            case InventoryData:
                if ((int) data[index] != this.clientId) {
                    disconnect("Client ID mismatch.");

                    break;
                }

                PlayerInventory inv = player.getInventory();

                int slot = data[1];
                int amount = data[2];
                String itemName;

                byte[] bn = new byte[data.length - 3];
                System.arraycopy(data, 3, bn, 0, bn.length);
                itemName = new String(bn);

                inv.setSlot(slot, new ItemStack(ItemType.lookup(itemName), amount));

                break;
            case RequestWorldData:
                response = new Packet(Protocol.WorldData);
                World world = server.getWorld();

                response.append(BitConverter.toBytes((int) world.getTime()));
                response.append(BitConverter.toBytes(world.isDay() ? 1 : 0)[ 0]); // Day
                response.append(BitConverter.toBytes(world.getMoonPhase().getState())[ 0]); // Moon phase
                response.append(BitConverter.toBytes(world.isBloodmoon() ? 1 : 0)[ 0]); // Bloodmoon

                response.append(BitConverter.toBytes(world.getWidth()));
                response.append(BitConverter.toBytes(60));

                Point spawn = world.getSpawn();
                response.append(BitConverter.toBytes(spawn.x));
                response.append(BitConverter.toBytes(spawn.y));

                response.append(BitConverter.toBytes(world.getDirtLayer()));
                response.append(BitConverter.toBytes(world.getRockLayer()));

                response.append(BitConverter.toBytes(0)); // World ID
                response.append(world.getWorldName().getBytes());

                break;
            case TileBlockRequest:
                if (loadedTiles == null) {
                    loadedTiles = new boolean[getServer().getWorld().getWidth() / 200][getServer().getWorld().getHeight() / 150];
                }
                int x = BitConverter.toInteger(data, index, 4);
                int y = BitConverter.toInteger(data, index + 4, 4);

                System.out.println("Requesting tile " + x + ":" + y);
                boolean flag2 = true;
                if ((x == -1) || (y == -1)) {
                    flag2 = false;
                } else if ((x < 10) || (x > (server.getWorld().getWidth() - 10))) {
                    flag2 = false;
                } else if ((y < 10) || (y > (server.getWorld().getHeight() - 10))) {
                    flag2 = false;
                }
                int num16 = 1350;
                if (flag2) {
                    num16 *= 2;
                }
                response = new Packet(Protocol.TileLoading);
                response.append(BitConverter.toBytes(num16));
                response.append("Receiving tile data".getBytes());
                write(response.create());

                int sectionX = World.getSectionX(getServer().getSpawnLocation().getXTile());
                int sectionY = World.getSectionX(getServer().getSpawnLocation().getXTile());

                break;
            case PlayerHealthUpdate:
                if ((int) data[index] != this.clientId) {
                    disconnect("Client ID mismatch.");

                    break;
                }
                index++;

                player.setStat(PlayerStat.Health, BitConverter.toInteger(data, index, 2));
                index += 2;

                player.setStat(PlayerStat.MaxHealth, BitConverter.toInteger(data, index, 2));

                break;

            case PlayerManaUpdate:
                if ((int) data[index] != this.clientId) {
                    disconnect("Client ID mismatch.");

                    break;
                }
                index++;

                player.setStat(PlayerStat.Mana, BitConverter.toInteger(data, index, 2));
                index += 2;

                player.setStat(PlayerStat.MaxMana, BitConverter.toInteger(data, index, 2));

                break;

            case PasswordResponse:
                if (server.usingPassword()) {
                    String password = new String(packet.getData());

                    if (password == null || !password.equals(server.getPassword())) {
                        disconnect("Incorrect password.");
                    } else {
                        authenticated = true;

                        response = new Packet(Protocol.RequestPlayerData);
                        response.append(BitConverter.toBytes(clientId));
                    }

                    break;
                }

            default:
                disconnect("Illegal packet received.");

                break;
        }

        if (response != null) {
            Logger.log(LogLevel.DEBUG, "Sent " + response.getProtocol().toString() + " packet. [" + this.toString() + "]"); // Should be moved to write()

            write(response.create());
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        if (player.getName() != null) {
            buffer.append(player.getName() + "@");
        }

        buffer.append(socket.getRemoteSocketAddress().toString().substring(1));

        return buffer.toString();
    }
}
