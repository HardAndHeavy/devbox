class QrController < ApplicationController
  def generate
    data = params[:data] || 'Default data'  # Получаем data из query-параметра, например ?data=123abc
    
    qr = RQRCode::QRCode.new(data)  # Генерируем QR-код
    
    # Рендерим как SVG (или PNG, если добавите chunky_png)
    svg = qr.as_svg(
      offset: 0,
      color: '000',
      shape_rendering: 'crispEdges',
      module_size: 6,
      standalone: true
    )
    
    respond_to do |format|
      format.svg { render xml: svg }  # Возвращаем SVG
    end
  end
end
