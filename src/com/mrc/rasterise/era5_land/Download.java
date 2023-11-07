package com.mrc.rasterise.era5_land;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

public class Download {

  private static String leadingZero(int m) {
    return ((m<10) ? "0" : "") + String.valueOf(m);
  }


  private static void writeDownload(String variable, String year, String month, String day, String time, int proc,
                                    String file, String workingDir) throws Exception {

    PrintWriter PW = new PrintWriter(new File(workingDir + "fetch"+proc+".py"));
    PW.println("import cdsapi");
    PW.println("c = cdsapi.Client()");
    PW.println("c.retrieve('reanalysis-era5-land', {");
    PW.println("             'variable': "+variable+",");
    PW.println("             'year' : '"+year+"',");
    PW.println("             'month' : '"+month+"',");
    PW.println("             'day' : '"+day+"',");
    if (time != null) PW.println("             'time' : "+time+",");
    PW.println("             'format': 'grib'");
    PW.println("            }, '"+workingDir+file+"')");
    PW.close();
  }

  static Process[] processes = new Process[3];

  private static void pull_data(int min_year, int max_year, String workingDir) throws Exception {
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTimeInMillis(System.currentTimeMillis());
    gc.set(GregorianCalendar.DAY_OF_MONTH,  15);


    for (int year = min_year; year <= max_year; year++) {
      gc.set(GregorianCalendar.YEAR, year);
      for (int month = 1; month <= 12; month++) {
        gc.set(GregorianCalendar.DAY_OF_MONTH,  1);
        gc.set(GregorianCalendar.MONTH, month - 1);
        int no_days = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        for (int day = 1; day <= no_days; day++) {
          gc.set(GregorianCalendar.DAY_OF_MONTH, day);
          for (int type = 0; type <= 1; type++) {
            int proc = -1;
            while (proc == -1) {
              for (int p = 0; p < processes.length; p++) {
                if ((processes[p] == null) || (!processes[p].isAlive())) {
                  proc = p;
                  break;
                }
              }
              if (proc == -1) Thread.sleep(10000);
            }

            if (type == 0) {
              writeDownload("'total_precipitation'", String.valueOf(year), leadingZero(month), leadingZero(day), "'00:00'",
                  proc, "precip_" + year + leadingZero(month)+leadingZero(day) + ".grib", workingDir);
            } else {
              writeDownload("['2m_temperature', '2m_dewpoint_temperature']", String.valueOf(year), leadingZero(month), leadingZero(day),
                  "['00:00', '01:00', '02:00', '03:00', '04:00', '05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', "+
                  "'12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00']", proc,
                  "temp_" + year + leadingZero(month) + leadingZero(day) + ".grib", workingDir);
            }
            PrintWriter PW = new PrintWriter(new FileWriter(workingDir+"run"+proc+".bat"));
            PW.println("python -u " + workingDir + "fetch"+proc+".py > " + workingDir+"out"+proc+".txt 2>&1");
            PW.close();
            processes[proc] = Runtime.getRuntime().exec(workingDir+"run"+proc+".bat");

          }
        }
      }
    }
  }

}
