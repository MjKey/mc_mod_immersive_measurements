package ru.alextrof94.immersive_measurements;

import java.util.List;

public class CustomData {
    public boolean isTemporary;
    public List<String> data;
    public float progress;

    public CustomData(boolean isTemporary, List<String> data) {
        this.isTemporary = isTemporary;
        this.data = data;
    }

    public CustomData(boolean isTemporary, List<String> data, float progress) {
        this.isTemporary = isTemporary;
        this.data = data;
        this.progress = progress;
    }
}
