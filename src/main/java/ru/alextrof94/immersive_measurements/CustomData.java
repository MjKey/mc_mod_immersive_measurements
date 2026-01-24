package ru.alextrof94.immersive_measurements;

import java.util.ArrayList;
import java.util.List;

public class CustomData {
    public boolean isTemporary = false;
    public List<String> data = new ArrayList<>();

    public CustomData() {}
    public CustomData(boolean isTemporary, List<String> data) {
        this.isTemporary = isTemporary;
        this.data = data;
    }
}
