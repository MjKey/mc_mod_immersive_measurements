package ru.alextrof94.immersive_measurements;

import java.util.ArrayList;
import java.util.List;

public class CustomData {
    public boolean isTemporary;
    public List<String> data;

    public CustomData(boolean isTemporary, List<String> data) {
        this.isTemporary = isTemporary;
        this.data = data;
    }
}
