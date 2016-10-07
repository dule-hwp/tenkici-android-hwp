package gamengine.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import tankgame.R;
import tankgame.TankWorld;

/**
 * Created by dusan_cvetkovic on 11/13/15.
 */
public class Utils {

    /**
     * @param context
     * @return hashmap that contains data read from file
     */
    public static HashMap<String, ArrayList<int[]>> getHashConfigFromConfigFile(Context context) {
        HashMap<String, ArrayList<int[]>> configFile = new HashMap<>();
        float density = context.getResources().getDisplayMetrics().density;
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.battlefield);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("//") || strLine.startsWith("#")) {
                    continue;
                }
                String[] splittedLine = strLine.split(" ");
                int[] values = new int[splittedLine.length - 1];
                for (int j = 1; j < splittedLine.length; j++) {
                    values[j - 1] = Integer.parseInt(splittedLine[j]);
                    if (j < 3)
                        values[j - 1] *= density;
                }

                ArrayList<int[]> alIntegerValues = configFile.get(splittedLine[0]);
                if (alIntegerValues == null) {
                    alIntegerValues = new ArrayList<>();
                } else {
                    alIntegerValues = configFile.get(splittedLine[0]);
                }
                alIntegerValues.add(values);
                configFile.put(splittedLine[0], alIntegerValues);

            }
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return configFile;
    }

    public static Region getRotatedRegionForGivenRect(Rect r, float angle, Point rotatePoint, Region clip) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle, rotatePoint.x, rotatePoint.y);

        Path path = new Path();
        Point LT = rotatePoint(matrix, r.left, r.top);
        Point RT = rotatePoint(matrix, r.right, r.top);
        Point RB = rotatePoint(matrix, r.right, r.bottom);
        Point LB = rotatePoint(matrix, r.left, r.bottom);

        path.moveTo(LT.x, LT.y);
        path.lineTo(RT.x, RT.y);
        path.lineTo(RB.x, RB.y);
        path.lineTo(LB.x, LB.y);
        path.lineTo(LT.x, LT.y);

        Region region = new Region();
        region.setPath(path, clip);

        return region;
    }

    private static Point rotatePoint(Matrix matrix, float x, float y) {
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;
        matrix.mapPoints(pts);
        return new Point((int) pts[0], (int) pts[1]);
    }

//    public static byte[] serialize(Object obj) {
//        ByteArrayOutputStream b = new ByteArrayOutputStream();
//        ObjectOutputStream o = null;
//        try {
//            o = new ObjectOutputStream(b);
//            o.writeObject(obj);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return b.toByteArray();
//    }

//    public static Object deserialize(byte[] bytes) {
//        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
//        try {
//            ObjectInputStream o = new ObjectInputStream(b);
//            return o.readObject();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
        public static Bitmap getWeaponImage(int weaponID) {
            if (weaponID<0)
                return null;
            Bitmap biWeapons = TankWorld.sprites.get("weapons");
            int boxSize = biWeapons.getHeight();
            Bitmap img = Bitmap.createBitmap(biWeapons, weaponID*boxSize, 0, boxSize, boxSize);
        //        float density = TankWorld.density;
            return Bitmap.createScaledBitmap(img, img.getWidth(), img.getHeight(), false);
        }


}
