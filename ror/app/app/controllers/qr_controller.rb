
class QrController < ApplicationController
  def generate
    data = params[:data] || 'Default data'
    
    qr = RQRCode::QRCode.new(data)
    
    png = qr.as_png(
      bit_depth: 1,
      border_modules: 0,
      color_mode: ChunkyPNG::COLOR_GRAYSCALE,
      color: 'black',
      file: nil,
      fill: 'white',
      module_px_size: 6,
      size: 120,
      uppercase: true
    )
    
    respond_to do |format|
      format.png { send_data png.to_s, type: 'image/png', disposition: 'inline' }
    end
  end
end

