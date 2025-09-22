from enum import Enum
from io import BytesIO
from typing import Annotated

import segno
from fastapi import FastAPI, Query, Response

app = FastAPI()


class ErrorCorrectionLevel(str, Enum):
    L = "L"
    M = "M"
    Q = "Q"
    H = "H"


DEFAULT_SCALE = 10
DEFAULT_BORDER = 4
DEFAULT_ERROR = ErrorCorrectionLevel.M
DEFAULT_MICRO = False


@app.get("/")
async def root():
    return "This is the FastAPI in DevBox."


@app.get("/qr")
def generate_qr(
    data: Annotated[str, Query(description="Data to encode in QR")],
    scale: Annotated[int, Query(description="Image scale")] = DEFAULT_SCALE,
    border: Annotated[int, Query(description="Border size")] = DEFAULT_BORDER,
    error: Annotated[
        ErrorCorrectionLevel,
        Query(description="Error correction level: L, M, Q, H"),
    ] = DEFAULT_ERROR,
    micro: Annotated[bool, Query(description="Use Micro QR")] = DEFAULT_MICRO,
):
    qr = segno.make(data, error=error.value, micro=micro)
    buf = BytesIO()
    qr.save(buf, kind="png", scale=scale, border=border)
    buf.seek(0)
    return Response(content=buf.read(), media_type="image/png")
