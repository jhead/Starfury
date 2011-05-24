package net.dv90.starfury.inventory;

public class ItemStack {

    public static final int MAX_STACK_SIZE = 250;

    private ItemType type;
    private int amount;

    public ItemStack(ItemType type, int amount)
    {
        this.type = type;
        this.amount = Math.min( amount, ItemStack.MAX_STACK_SIZE );
    }

    public int getAmount()
    {
        return this.amount;
    }

    public void setAmount(int amount)
    {
        this.amount = Math.min( amount, ItemStack.MAX_STACK_SIZE );
    }

    public ItemType getType()
    {
        return this.type;
    }

    public void setType(ItemType type)
    {
        this.type = type;
    }

}
