package net.dv90.starfury.inventory;

import java.util.ArrayList;

public class PlayerInventory {

    private ArrayList<ItemStack> slots;

    public PlayerInventory()
    {
        slots = new ArrayList<ItemStack>();
    }

    public ItemStack getSlot(int slot)
    {
        return slots.get(slot);
    }

    public void setSlot(int slot, ItemStack item)
    {
        slots.set(slot, item);
    }

    public int add(ItemStack item)
    {
        for( int i = 0; i < slots.size(); i++ )
        {
            if( slots.get(i) == null )
            {
                slots.set(i, item);
                return i;
            }
        }

        return -1;
    }

    public void clear(int slot)
    {
        this.setSlot(slot, new ItemStack(ItemType.Empty, 0));
    }

    public void remove(ItemStack item)
    {
        int a = item.getAmount();

        while( a > 0 )
        {
            for( ItemStack is : slots )
            {
                if( is.getType().equals(item.getType()) )
                {
                    while( is.getAmount() > 0 && a > 0)
                    {
                        is.setAmount(is.getAmount() - 1);
                        a--;
                    }
                }
            }
        }
    }

}
