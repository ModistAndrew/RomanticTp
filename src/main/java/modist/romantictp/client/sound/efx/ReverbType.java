package modist.romantictp.client.sound.efx;

public enum ReverbType {
    EMPTY, TEST;

    public static ReverbType fromString(String name){
        try {
            return valueOf(name);
        } catch (Exception e) {
            return EMPTY;
        }
    }
}
