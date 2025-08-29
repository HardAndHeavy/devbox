from fastapi import FastAPI, Query, Response
import segno
from io import BytesIO

app = FastAPI()


@app.get("/")
async def root():
    return "This is the FastAPI in DevBox."

@app.get("/qr")
def generate_qr(
    data: str = Query(..., description="Data to encode in QR"),
    scale: int = Query(10, description="Image scale"),
    border: int = Query(4, description="Border size"),
    error: str = Query("M", description="Error correction level: L, M, Q, H"),
    micro: bool = Query(False, description="Use Micro QR")
):
    qr = segno.make(data, error=error, micro=micro)
    buf = BytesIO()
    qr.save(buf, kind='png', scale=scale, border=border)
    buf.seek(0)
    return Response(content=buf.read(), media_type="image/png")
