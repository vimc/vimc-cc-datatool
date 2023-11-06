package com.mrc.rasterise.era5_land;

public class AdminUnitStats {
  double mint_lo, mint_avg, mint_hi, meant_lo, meant_avg, meant_hi, maxt_lo, maxt_avg, maxt_hi;
  double prec_lo, prec_avg, prec_hi;
  double minr_lo, minr_avg, minr_hi, meanr_lo, meanr_avg, meanr_hi, maxr_lo, maxr_avg, maxr_hi;
  
  int count;
  void init() {
    count = 0;
    mint_lo = Double.POSITIVE_INFINITY;
    mint_avg = 0;
    mint_hi = Double.NEGATIVE_INFINITY;
    meant_lo = Double.POSITIVE_INFINITY;
    meant_avg = 0;
    meant_hi = Double.NEGATIVE_INFINITY;
    maxt_lo = Double.POSITIVE_INFINITY;
    maxt_avg = 0;
    maxt_hi = Double.NEGATIVE_INFINITY;
    minr_lo = Double.POSITIVE_INFINITY;
    minr_avg = 0;
    minr_hi = Double.NEGATIVE_INFINITY;
    meanr_lo = Double.POSITIVE_INFINITY;
    meanr_avg = 0;
    meanr_hi = Double.NEGATIVE_INFINITY;
    maxr_lo = Double.POSITIVE_INFINITY;
    maxr_avg = 0;
    maxr_hi = Double.NEGATIVE_INFINITY;
    prec_lo = Double.POSITIVE_INFINITY;
    prec_avg = 0;
    prec_hi = Double.NEGATIVE_INFINITY;
        
  }
  
  AdminUnitStats() {
    init();
  }
  
  void update(OneDay D, int x, int y, int pop) {
    count+=pop;
    mint_lo = Math.min(mint_lo, D.tm_min[x][y]);
    mint_avg += (D.tm_min[x][y] * pop);
    mint_hi = Math.max(mint_hi, D.tm_min[x][y]);
    meant_lo = Math.min(meant_lo, D.tm_mean[x][y]);
    meant_avg += (D.tm_mean[x][y] * pop);
    meant_hi = Math.max(meant_hi, D.tm_mean[x][y]);
    maxt_lo = Math.min(maxt_lo, D.tm_max[x][y]);
    maxt_avg += (D.tm_max[x][y] * pop);
    maxt_hi = Math.max(maxt_hi, D.tm_max[x][y]);
    
    minr_lo = Math.min(minr_lo, D.rh_min[x][y]);
    minr_avg += (D.rh_min[x][y] * pop);
    minr_hi = Math.max(minr_hi, D.rh_min[x][y]);
    meanr_lo = Math.min(meanr_lo, D.rh_mean[x][y]);
    meanr_avg += (D.rh_mean[x][y] * pop);
    meanr_hi = Math.max(meanr_hi, D.rh_mean[x][y]);
    maxr_lo = Math.min(maxr_lo, D.rh_max[x][y]);
    maxr_avg += (D.rh_max[x][y] * pop);
    maxr_hi = Math.max(maxr_hi, D.rh_max[x][y]);
    
    prec_lo = Math.min(prec_lo, D.precip[x][y]);
    prec_avg += (D.precip[x][y] * pop);
    prec_hi = Math.max(prec_hi, D.precip[x][y]);
          
  }
  
  void finalise() {
    mint_avg /= (double) count;
    meant_avg /= (double) count;
    maxt_avg /= (double) count;
    prec_avg /= (double) count;
    minr_avg /= (double) count;
    meanr_avg /= (double) count;
    maxr_avg /= (double) count;
  }
}
