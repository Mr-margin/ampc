load "$NCARG_ROOT/lib/ncarg/nclscripts/csm/gsn_code.ncl"
load "$NCARG_ROOT/lib/ncarg/nclscripts/csm/gsn_csm.ncl"
load "$NCARG_ROOT/lib/ncarg/nclscripts/wrf/WRF_contributed.ncl"
load "$NCARG_ROOT/lib/ncarg/nclscripts/csm/contributed.ncl"

begin

   InDataFile = getenv("VertInDataFile")
   InColorFile = getenv("VertInColorFile")
   fon = getenv("VertOutFileName")

   str_color = asciiread(InColorFile,-1,"string")
   cnlevel2 = tofloat(str_split_csv(str_color,",",0))
   cnlevel = rm_single_dims(cnlevel2)

   str_array = asciiread(InDataFile,-1,"string")
   XX = tofloat(str_split_csv(str_array,",",0))
   XX@_FillValue = -9999.0
   XX = where(XX.eq.-1e9,XX@_FillValue,XX)
   ndimsizes = dimsizes(XX)
   nHgt = ndimsizes(0)
   nlen = ndimsizes(1)

   HgtInterval = 50     ;;; m
   HgtMaxDisplay = HgtInterval*(nHgt-1)
   HgtSpaceDisplay = HgtMaxDisplay / 10    ;;;
   HgtDisplay = new(nHgt,"float")
   do ih = 0,nHgt-1
     HgtDisplay(ih) = ih * HgtInterval
   end do

   latlon = new(nlen,"float")
   do il=0,nlen-1
     latlon(il) = il
   end do

   XX!0 = "height"
   XX&height = HgtDisplay
   XX!1 = "latlon"
   XX&latlon = latlon

   wks = gsn_open_wks("eps",fon)
   clbdft = "modelcloud"
   gsn_define_colormap(wks,clbdft)

   res = True
   res@vpXF = 0.2
   res@vpYF = 0.75
   res@vpWidthF = 0.6              ; change aspect ratio of plot
   res@vpHeightF  = 0.4
   res@cnFillOn = True
   res@cnLinesOn = False
   res@cnLineLabelsOn = False

   res@tmYLLabelsOn = True
   res@tmYLMinorOn = False
   res@tmXBOn = False
   res@tmXTOn = False
   res@tmYROn = False

   res@tmYLMode   = "Manual"
   res@tmYLTickStartF = 0
   res@tmYLTickEndF = HgtMaxDisplay
   res@tmYLTickSpacingF = HgtSpaceDisplay
   res@tmYLLabelFontHeightF = 0.016

   res@tiYAxisString = "Height (m)" ;yaxis string

   res@cnLevelSelectionMode = "ExplicitLevels"
   res@cnLevels = cnlevel
   res@lbLabelStride = 5

   plot = gsn_csm_contour(wks,XX,res)
 ;  system("convert -density 300 -trim "+fon+".eps "+fon+".png")
   delete(XX)

end
