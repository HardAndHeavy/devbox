defmodule AppWeb.QrController do
  use AppWeb, :controller

  def generate(conn, %{"data" => data}) when is_binary(data) do
    qr_code = EQRCode.encode(data)
    png_data = EQRCode.png(qr_code)

    conn
    |> put_resp_content_type("image/png", nil)
    |> send_resp(200, png_data)
  end

  def generate(conn, %{"data" => _invalid_data}) do
    conn
    |> send_resp(400, "Parameter 'data' must be a valid string")
  end

  def generate(conn, _params) do
    conn
    |> send_resp(400, "Parameter 'data' is required")
  end
end
