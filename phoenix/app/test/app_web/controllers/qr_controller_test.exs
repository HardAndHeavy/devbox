defmodule AppWeb.QrControllerTest do
  use AppWeb.ConnCase, async: true

  describe "generate/2" do
    test "returns PNG image with valid QR code when data parameter is provided", %{conn: conn} do
      test_data = "https://example.com"

      conn = get(conn, ~p"/qr", %{"data" => test_data})

      assert response_content_type(conn, :png)
      assert conn.status == 200

      assert <<0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, _rest::binary>> = conn.resp_body
    end

    test "returns 400 error when data parameter is missing", %{conn: conn} do
      conn = get(conn, ~p"/qr")

      assert conn.status == 400
      assert conn.resp_body == "Parameter 'data' is required"
    end

    test "returns 400 error when params are empty map", %{conn: conn} do
      conn = get(conn, ~p"/qr", %{})

      assert conn.status == 400
      assert conn.resp_body == "Parameter 'data' is required"
    end

    test "generates QR code for empty string", %{conn: conn} do
      conn = get(conn, ~p"/qr", %{"data" => ""})

      assert response_content_type(conn, :png)
      assert conn.status == 200
    end

    test "generates QR code for long text", %{conn: conn} do
      long_text = String.duplicate("a", 1000)

      conn = get(conn, ~p"/qr", %{"data" => long_text})

      assert response_content_type(conn, :png)
      assert conn.status == 200
    end

    test "generates QR code with special characters", %{conn: conn} do
      special_text = "Hello! @#$%^&*()_+ 世界 🎉"

      conn = get(conn, ~p"/qr", %{"data" => special_text})

      assert response_content_type(conn, :png)
      assert conn.status == 200
    end

    test "generates QR code with URL containing query parameters", %{conn: conn} do
      url_with_params = "https://example.com/path?param1=value1&param2=value2"

      conn = get(conn, ~p"/qr", %{"data" => url_with_params})

      assert response_content_type(conn, :png)
      assert conn.status == 200
    end

    test "ignores additional parameters when data is present", %{conn: conn} do
      conn = get(conn, ~p"/qr", %{"data" => "test", "extra" => "param"})

      assert response_content_type(conn, :png)
      assert conn.status == 200
    end
  end

  describe "generate/2 with property-based testing" do
    @tag :property
    test "always returns PNG for any valid string data", %{conn: conn} do
      use ExUnitProperties

      check all(data <- string(:printable, min_length: 1, max_length: 500)) do
        conn = get(conn, ~p"/qr", %{"data" => data})

        assert response_content_type(conn, :png)
        assert conn.status == 200
      end
    end
  end
end
