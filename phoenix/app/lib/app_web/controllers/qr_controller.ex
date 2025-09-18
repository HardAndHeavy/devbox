defmodule AppWeb.QrController do
  use AppWeb, :controller

  def generate(conn, %{"data" => data}) do
    qr_code = EQRCode.encode(data)
    png_data = EQRCode.png(qr_code)

    conn
    |> put_resp_content_type("image/png")
    |> send_resp(200, png_data)
  end

  def generate(conn, _params) do
    conn
    |> send_resp(400, "Parameter 'data' is required")
  end
end
