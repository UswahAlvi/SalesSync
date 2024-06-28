package com.example.salessync;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {
    final int tabs=2;
    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new InventoryFragment();
        }
        return new SalesFragment();
    }

    @Override
    public int getItemCount() {
        return tabs;
    }
}
