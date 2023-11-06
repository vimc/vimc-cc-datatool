package com.mrc.rasterise.era5_land;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import net.sourceforge.jgrib.GribFile;
import net.sourceforge.jgrib.GribRecord;

public class OneDay {
  float[][][] tm = new float[24][3600][1801];
  float[][] tm_mean = new float[3600][1801];
  float[][] tm_max = new float[3600][1801];
  float[][] tm_min = new float[3600][1801];
  float[][][] dp = new float[24][3600][1801];
  float[][][] rh = new float[24][3600][1801];
  float[][] rh_max = new float[3600][1801];
  float[][] rh_min = new float[3600][1801];
  float[][] rh_mean = new float[3600][1801];
  float[][] precip = new float[3600][1801];

  void make_image(float[][] data) throws Exception  {
    BufferedImage bi = new BufferedImage(3600, 1801, BufferedImage.TYPE_3BYTE_BGR);
    float big = Float.NEGATIVE_INFINITY;
    float small = Float.POSITIVE_INFINITY;
    for (int j=0; j<1801; j++) 
      for (int i=0; i<3600; i++) {
        big = Math.max(big,  data[i][j]);
        small = Math.min(small,  data[i][j]);
     }
    
    float range = big - small;
    
    int[] cc = new int[256];
    for (int i=0; i<256; i++) cc[i] = new Color(i, i, i).getRGB();
    
    for (int j=0; j<1801; j++) 
      for (int i=0; i<3600; i++) {
        int col = (int) (((data[i][j] - small) / range) * 255);
        bi.setRGB(i, j, cc[col]);
      }
  
    ImageIO.write(bi,  "PNG",  new File("test_temp.png"));
  }
  
  void parse_record(int hour, GribRecord g) throws Exception {
    if (g.getDescription().equals("2 metre temperature")) {
      for (int x = 0; x < 3600; x++)
        for (int y = 0; y < 1801; y++)
          tm[hour][x][y] = g.getValue(x,  y) - 273.16f;
      
      System.out.print("+");
    
    } else if (g.getDescription().equals("2 metre dewpoint temperature")) {
      for (int x = 0; x < 3600; x++) 
        for (int y = 0; y < 1801; y++) 
          dp[hour][x][y] = g.getValue(x,  y) - 273.16f;
    
    } else if (g.getDescription().equals("Total precipitation")) {
      for (int i = 0; i < 3600; i++) 
        for (int j = 0; j < 1801; j++) {
          precip[i][j] = g.getValue(i, j);
          if (precip[i][j] > 1E20) precip[i][j] = Float.NaN;
        }
    
    } else {
      System.out.println("ERROR - variable not recognised - "+g.getDescription());
    } 
  }

  // Calculate relative humidity using August-Roche-Magnus equation from T and Td.
  // See https://qed.epa.gov/hms/meteorology/humidity/algorithms/
  
  void calc_rh() {
    for (int h = 0; h < 24; h++) 
      for (int x = 0; x < 3600; x++) 
        for (int y = 0; y < 1801; y++) 
          rh[h][x][y] = ((tm[h][x][y] > 1E18) || (dp[h][x][y] > 1E18)) ? Float.POSITIVE_INFINITY :
            (float) (100.0*(Math.exp((17.625 * dp[h][x][y]) / (243.04 + dp[h][x][y])) / 
                            Math.exp((17.625 * tm[h][x][y]) / (243.04 + tm[h][x][y]))));
  }


  void calc_min_max_mean() {
    for (int i = 0; i < 3600; i++) {
      for (int j = 0; j < 1801; j++) {
        tm_min[i][j] = Float.MAX_VALUE;
        tm_max[i][j] = Float.NEGATIVE_INFINITY;
        int tm_counter=0;
        tm_mean[i][j] = 0;
        rh_min[i][j] = Float.MAX_VALUE;
        rh_max[i][j] = Float.NEGATIVE_INFINITY;
        int rh_counter = 0;
        rh_max[i][j] = 0;
        for (int h = 0; h < 24; h++) {
          if (tm[h][i][j] < 1E20) {
            tm_counter = tm_counter + 1;
            tm_mean[i][j] = tm_mean[i][j] + tm[h][i][j];
            tm_max[i][j] = Math.max(tm_max[i][j], tm[h][i][j]);
            tm_min[i][j] = Math.min(tm_min[i][j], tm[h][i][j]);
          }
          if ((rh[h][i][j] >= 0) && (rh[h][i][j] <= 100)) {
            if (rh[h][i][j] < 1E20) {
              rh_counter = rh_counter + 1;
              rh_mean[i][j] = rh_mean[i][j] + rh[h][i][j];
              rh_max[i][j] = Math.max(rh_max[i][j], rh[h][i][j]);
              rh_min[i][j] = Math.min(rh_min[i][j], rh[h][i][j]);
            }
          }
        }
        tm_mean[i][j] = tm_mean[i][j] / (float) tm_counter;
        rh_mean[i][j] = rh_mean[i][j] / (float) rh_counter;
        if (tm_counter == 0) {
          tm_mean[i][j] = Float.NaN;
          tm_max[i][j] = Float.NaN;
          tm_min[i][j] = Float.NaN;
        }
        if (rh_counter == 0) {
          rh_mean[i][j] = Float.NaN;
          rh_max[i][j] = Float.NaN;
          rh_min[i][j] = Float.NaN;
        }
      }
    }
  }

  void find_nearest(int xp, int yp) {
    int range = 0;
    int count = 0;
    float maxt = 0, mint = 0, meant = 0, maxrh = 0, minrh = 0, meanrh = 0, prec = 0;
    while (count == 0) {
      range++;
      for (int i2 = xp - range; i2 <= xp + range; i2++) {
        for (int j2 = yp - range; j2 <= yp + range; j2++) {
          if (!Float.isNaN(tm_mean[i2][j2])) {
            count++;
            maxt += tm_max[i2][j2];
            mint += tm_min[i2][j2];
            meant += tm_mean[i2][j2];
            maxrh += rh_max[i2][j2];
            minrh += rh_min[i2][j2];
            meanrh += rh_mean[i2][j2];
            prec += precip[i2][j2];
          }
        }
      }
    }
    tm_mean[xp][yp] = meant / count;
    tm_max[xp][yp] = maxt / count;
    tm_min[xp][yp] = mint / count;
    rh_mean[xp][yp] = meanrh / count;
    rh_max[xp][yp] = maxrh / count;
    rh_min[xp][yp] = minrh / count;
    precip[xp][yp] = prec / count;
  }
  
  
  
  public static OneDay get_day_data(String temp_path, String precip_path, int year, int month, int day) throws Exception {
    OneDay D = new OneDay();
    String grib_file = temp_path + File.separator + "temp_"+year+((month < 10)?"0":"") + month + ((day < 10) ? "0":"") + day + ".grib";
      
    System.out.print(year+"-"+((month < 10)?"0":"") + month + "-" + ((day < 10) ? "0":"") + day + " : ");
      
    if (!new File(grib_file).exists()) return null;

    GribFile grb = new GribFile(grib_file);
      
    int no_recs = grb.getRecordCount();
    for (int i = 1; i <= no_recs; i++) {
      GribRecord grecord = grb.getRecord(i);
      Calendar cal = grecord.getTime();
      int hour = cal.get(Calendar.HOUR_OF_DAY);
      D.parse_record(hour, grecord);
    }
    
    D.calc_rh();
    D.calc_min_max_mean();    
    
    // Min, max, mean
        
    // Total precipitation at 00:00 is the total precipitation in the previous 24 hours.
    // The counter is then reset, and at 01:00 the numbers will be zero and rising again.
    // Note that months start at zero in GregorianCalendar.
    
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTimeInMillis(System.currentTimeMillis());
    gc.set(GregorianCalendar.DATE, 5);
    gc.set(GregorianCalendar.MONTH, 5);
    gc.set(GregorianCalendar.YEAR, year);
    gc.set(GregorianCalendar.MONTH, month - 1);
    gc.set(GregorianCalendar.DATE, day);
    gc.add(GregorianCalendar.DATE,  1);
    year = gc.get(GregorianCalendar.YEAR);
    month = gc.get(GregorianCalendar.MONTH) + 1;
    day = gc.get(GregorianCalendar.DATE);
    
    grib_file = precip_path + File.separator + "precip_"+year+((month < 10)?"0":"") + month + ((day < 10) ? "0":"") + day + ".grib";
    grb = new GribFile(grib_file);
    no_recs = grb.getRecordCount();
    if (no_recs != 1) System.out.println("Preciperror");
    D.parse_record(0, grb.getRecord(1));
    System.out.print("p");
    return D;

  }
}