package dimasg.drawi.schemes;

import java.io.Serializable;

public class Meta implements Serializable {

    public String label;
    public Long timestamp;
    public byte[] icon;

    public Meta(String label, Long timestamp, byte[] icon) {
        this.label = label;
        this.timestamp = timestamp;
        this.icon = icon;
    }

    public Meta() {
        this("", 0L, new byte[0]);
    }
}
