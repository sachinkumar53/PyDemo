import cv2
import numpy as np

def resize_gray(data):
    decoded = cv2.imdecode(np.frombuffer(data, np.uint8), -1)
    gray = cv2.cvtColor(decoded, cv2.COLOR_BGR2GRAY)
    resized = cv2.resize(gray, (500, 500))
    return cv2.imencode(".png", resized)[1].tobytes()