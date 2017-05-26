#!/bin/csh

setenv VertInDataFile  ${juzhenPath}
setenv VertInColorFile ${specieIndexFilePath}
setenv VertOutFileName ${pngPath}

ncl ${nclPath}
convert -density 300 -trim $VertOutFileName.eps $VertOutFileName.png
