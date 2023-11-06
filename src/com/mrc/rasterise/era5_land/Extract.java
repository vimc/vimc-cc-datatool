package com.mrc.rasterise.era5_land;

import java.io.File;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

public class Extract {
  
  public static void extract_point(String temp_path, String precip_path, float[] lon, float[] lat, 
                                   GregorianCalendar start, GregorianCalendar end,
                                   String out_csv) throws Exception {
    
    PrintWriter PW = new PrintWriter(new File(out_csv));
    PW.println("lon,lat,year,month,day,t_min,t_mean,t_max,rh_min,rh_mean,rh_max,precip");
    GregorianCalendar now = start;
    
    int[] ls_to_era5_x = Coords.ls_to_era5_x();
    int[] ls_to_era5_y = Coords.ls_to_era5_y();
    
    
    while (!now.after(end)) {
      int year = now.get(GregorianCalendar.YEAR);
      int month = now.get(GregorianCalendar.MONTH) + 1;
      int day = now.get(GregorianCalendar.DAY_OF_MONTH);
      OneDay day_data = OneDay.get_day_data(temp_path, precip_path, year, month, day);
      for (int p = 0; p < lon.length; p++) {
        int era_x = ls_to_era5_x[Coords.lon_to_ls(lon[p])];
        int era_y = ls_to_era5_y[Coords.lat_to_ls(lat[p])];
        PW.println(lon[p] + "," + lat[p] + "," + year + "," + month + "," + day +"," +
          day_data.tm_min[era_x][era_y] + "," +
          day_data.tm_mean[era_x][era_y] + "," +
          day_data.tm_max[era_x][era_y] + "," +
          day_data.rh_min[era_x][era_y] + "," +
          day_data.rh_mean[era_x][era_y] + "," +
          day_data.rh_max[era_x][era_y] + "," +
          day_data.precip[era_x][era_y]);
      }
      PW.flush();
    
      now.add(GregorianCalendar.DATE,  1);
    }
    PW.close();
  }
  
}
