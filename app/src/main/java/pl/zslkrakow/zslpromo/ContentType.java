package pl.zslkrakow.zslpromo;

/**
 * Created by Jacek on 16.03.2016.
 */
public class ContentType {
    public static final int IT = 1;
    public static final int EE = 2;
    public static final int TELE = 3;

    public static String getFieldName(int type) {
        switch(type) {
            case 1:
                return "Informatyka";
            case 2:
                return "Elektronika";
            case 3:
                return "Teleinformatyka/Telekomunikacja";
            default:
                return "Nieznane";
        }
    }
}
