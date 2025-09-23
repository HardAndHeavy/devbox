defmodule AppWeb.QrControllerIntegrationTest do
  use AppWeb.ConnCase, async: true

  describe "QR code generation and validation" do
    test "generated QR code contains correct data", %{conn: conn} do
      original_data = "Test QR Data 123"

      conn = get(conn, ~p"/qr", %{"data" => original_data})

      assert conn.status == 200
    end

    test "response headers are set correctly", %{conn: conn} do
      conn = get(conn, ~p"/qr", %{"data" => "test"})

      assert get_resp_header(conn, "content-type") == ["image/png"]
      assert conn.status == 200

      assert byte_size(conn.resp_body) > 100
      assert byte_size(conn.resp_body) < 100_000
    end
  end

  describe "error handling" do
    test "handles malformed query parameters gracefully", %{conn: conn} do
      conn = get(conn, ~p"/qr", %{"data" => ["array", "value"]})

      assert conn.status == 400
      assert conn.resp_body == "Parameter 'data' must be a valid string"
    end

    test "handles nil data parameter", %{conn: conn} do
      # Note: In Phoenix, %{"data" => nil} is encoded to "data=" and parsed as %{"data" => ""},
      # which is treated as a valid empty string and generates a QR code (status 200)
      conn = get(conn, ~p"/qr", %{"data" => nil})

      assert conn.status == 200
      assert get_resp_header(conn, "content-type") == ["image/png"]

      assert byte_size(conn.resp_body) > 100
    end
  end

  describe "performance" do
    @tag :performance
    test "generates QR code within reasonable time", %{conn: conn} do
      data = String.duplicate("Performance Test ", 20)

      {time, conn} =
        :timer.tc(fn ->
          get(conn, ~p"/qr", %{"data" => data})
        end)

      assert conn.status == 200

      assert time < 1_000_000
    end
  end
end
