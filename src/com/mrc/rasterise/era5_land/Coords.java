package com.mrc.rasterise.era5_land;

public class Coords {
  
  public static int lon_to_ls(float lon) {
    return (int) Math.floor((lon + 180.0) * 120.0);  
  }
  
  public static int lat_to_ls(float lat) {
    return (int) Math.floor((90 - lat) * 120.0);
    
  }
  
  public static int[] ls_to_era5_x() {
    // A lookup table to get around some awkward fencepost maths.
    // Landscan x=0 is the grid-square with longitude -180.0 ... -179.9917
    // ERA5 x=1800 is the point sample at -180,0.
    
    // Landscan cells with x = 0, 1, 2, 3, 4 and 5 are all closest to ERA5 where x = 1800.
    // Landscan cells with x = 6, 7, 8 ,9, 10, 11 are closest to ERA5 x=1801 - as are Landscan cells 12, 13, 14, 15, 16, 17.
    // Then landscan 18 is closest to 1802....
    
    // This continues all the way to ERA5 x=3599, which wraps to x=0, and then all the way to x=1799.
    // Finally, we have the last 6 cells on the extreme east of Landscan's world, which are closest to x=1800 in ERA5 world.
    
    int[] conv = new int[43200];
    for (int i=0; i<=5; i++) conv[i] = 1800;
    for (int i=43199; i>=43194; i--) conv[i] = 1800;
    int era5_x = 1801;
    for (int i=6; i<43194; i+=12) {
      for (int ii=i; ii<i+12; ii++) {
        conv[ii] = era5_x;
      }
      era5_x = (era5_x + 1) % 3600;
    }
    return conv;
  }
  
  public static int[] ls_to_era5_y() {
    // Similarly...ish
    // Landscan y=0 is the grid-square with lat +89.9917 ... +90
    // ERA5 y = 0 is at +90 - the top edge.
    // Landscan y = 0, 1, 2, 3, 4, 5 are closest to ERA5 y = 0
    // Landscan y = 6, 7, 8 , 9, 10, 11 are closest to ERA5 y = 1, as are ls_y = 12,13,14,15,16,17
    // At the bottom, Landscan y = 21594..21599 are closest to ERA5 y = 1801, which is on the -90 gridline.
    
    int[] conv = new int[21600];
    for (int j=0; j<=5; j++) conv[j] = 0;
    for (int j=21599; j>=21594; j--) conv[j] = 1801;
    int era5_y = 1;
    for (int j=6; j<21594; j+=12) {
      for (int jj=j; jj<j+12; jj++) {
        conv[jj] = era5_y;
      }
      era5_y++;
    }
    return conv;
  }
}
