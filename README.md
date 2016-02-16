MagicQRCode
=================================

# Introduction

**MagicQRCode** is a Java library used for handling QR Code. It can be easily used in JavaFX applications and Android applications.

# Usage

## QRCodeEncoder Class

**QRCodeEncoder** class is in the *org.magiclen.magicqrcode* package. It can encode any string to QR Code.

### Initialize

You can input any string you want to encode to the constructor of **QRCodeEncoder** class to create an instance.

    QRCodeEncoder qr = new QRCodeEncoder("your string");

### Encode Your String To QR Code

Just call the **encode** method. This method will return a two-dimension byte array.

    boolean[][] qrData = qr.encode();

### Set Error Correction Level

You can also set the error correction level of your QR Code by calling **setErrorCorrect** method before calling **encode** method.

    qr.setErrorCorrect(QRCodeEncoder.ErrorCorrect.MAX);
    boolean[][] qrData = qr.encode();

### Visualize Your QR Code

#### JavaFX

In JavaFX Application, you can use **Canvas** object and **GraphicsContext** object to draw a QR Code image. For example,

    public static void drawQRCode(final Canvas canvas, final boolean[][] qrData) {
    	final int width = (int)canvas.getWidth();
    	final int height = (int)canvas.getHeight();
    	final GraphicsContext gc = canvas.getGraphicsContext2D();

    	// Draw the background(white)
    	gc.setFill(Color.WHITE);
    	gc.fillRect(0, 0, width, height);

    	final int imageSize = Math.min(width, height);
    	final int length = qrData.length;
    	final int size = imageSize / length;
    	final int actualImageSize = size * length;
    	final int offsetImageX = (width - actualImageSize) / 2;
    	final int offsetImageY = (height - actualImageSize) / 2;

    	// Draw the data(true is black)
    	gc.setFill(Color.BLACK);
    	for (int i = 0; i < length; i++) {
    		for (int j = 0; j < length; j++) {
    			if (qrData[i][j]) {
    				final int x = i * size + offsetImageX;
    				final int y = j * size + offsetImageY;
    				gc.fillRect(x, y, size, size);
    			}
    		}
    	}
    }

#### Android

In Android Application, you can use **Canvas** object to draw a QR Code image. For example,

    public static void drawQRCode(final Canvas canvas, final boolean[][] qrData) {
    	final Paint paint = new Paint();
    	final int width = canvas.getWidth();
    	final int height = canvas.getHeight();

    	// Draw the background(white)
    	paint.setColor(Color.WHITE);
    	canvas.drawRect(new Rect(0, 0, width, height), paint);

    	final int imageSize = Math.min(width, height);
    	final int length = qrData.length;
    	final int size = imageSize / length;
    	final int actualImageSize = size * length;
    	final int offsetImageX = (width - actualImageSize) / 2;
    	final int offsetImageY = (height - actualImageSize) / 2;

    	// Draw the data(true is black)
    	paint.setColor(Color.BLACK);
    	for (int i = 0; i < length; i++) {
    		for (int j = 0; j < length; j++) {
    			if (qrData[i][j]) {
    				final int x = i * size + offsetImageX;
    				final int y = j * size + offsetImageY;
    				canvas.drawRect(new Rect(x, y, x + size, y + size), paint);
    			}
    		}
    	}
    }

# License

    Copyright 2015-2016 magiclen.org

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

# What's More?

Please check out our web page at

https://magiclen.org/java-qrcode/
