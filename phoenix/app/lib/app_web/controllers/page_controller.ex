defmodule AppWeb.PageController do
  use AppWeb, :controller

  def home(conn, _params) do
    text(conn, "This is the Phoenix in DevBox.")
  end
end
