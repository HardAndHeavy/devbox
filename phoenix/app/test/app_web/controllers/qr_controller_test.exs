defmodule AppWeb.QrControllerTest do
  use AppWeb.ConnCase

  describe "GET /qr" do
    test "generates QR code PNG when 'data' parameter is provided", %{conn: conn} do
      conn = get(conn, ~p"/qr", %{data: "https://example.com"})

      body = response(conn, 200)

      assert get_resp_header(conn, "content-type") == ["image/png"]
      assert is_binary(body)
      assert byte_size(body) > 0
    end

    test "returns 400 error when 'data' parameter is missing", %{conn: conn} do
      conn = get(conn, ~p"/qr")

      body = response(conn, 400)

      assert body == "Parameter 'data' is required"
    end
  end
end
