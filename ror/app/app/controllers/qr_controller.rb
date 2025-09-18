class QrController < ApplicationController
  def generate
    data = params[:data] || 'Default data'
    
    qr = RQRCode::QRCode.new(data)
    
    svg = qr.as_svg(
      offset: 0,
      color: '000',
      shape_rendering: 'crispEdges',
      module_size: 6,
      standalone: true
    )
    
    respond_to do |format|
      format.svg { render xml: svg }
    end
  end
end
