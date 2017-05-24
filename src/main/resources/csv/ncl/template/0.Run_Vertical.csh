#!/bin/csh

setenv VertInDataFile  ${shellVertival.juzhenPath}
setenv VertInColorFile ${shellVertival.specieIndexFilePath}
setenv VertOutFileName ${shellVertival.pngPath}

ncl ${shellVertival.nclPath}
convert -density 300 -trim $VertOutFileName.eps $VertOutFileName.png
