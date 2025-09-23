defmodule AppWeb.PageControllerTest do
  use AppWeb.ConnCase

  test "GET /", %{conn: conn} do
    conn = get(conn, ~p"/")
    assert response(conn, 200) == "This is the Phoenix in DevBox."
  end
end
