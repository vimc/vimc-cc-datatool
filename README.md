# vimc-cc-datatool

<!-- badgets: start -->
[![Run Tests](https://github.com/vimc/vimc-cc-datatool/actions/workflows/tests.yml/badge.svg?branch=main)](https://github.com/vimc/vimc-cc-datatool/actions/workflows/tests.yml)
<!-- badges: end -->

This Java repo aims to deal with some common requests for various sources of spatial data 
(eg, population, climate, land use) from various sources (eg, Landscan, ERA5, MODIS), to
be read and processed, perhaps aggregated to a particular admin unit level from a shapefile,
perhaps population-weighted, with the result exported as some kind of CSV-like file, 
including as inputs for [covid-sim](https://github.com/mrc-ide/covid-sim).
