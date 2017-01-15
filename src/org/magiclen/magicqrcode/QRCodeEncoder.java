/*
 *
 * Copyright 2015-2016 magiclen.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.magiclen.magicqrcode;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * <p>
 * QR Code編碼工具。
 * </p>
 *
 * <p>
 * 原始碼和資料來源：http://www.swetake.com/qrcode/
 * </p>
 * 
 * <p>
 * QRcode class library 0.50beta10</p>
 * <p>
 * (c)2003-2005 Y.Swetake, 2015-2016 magiclen.org</p>
 * <p>
 * This version supports QRcode model2 version 1-40.</p>
 * <p>
 * Some functions are not supported.</p>
 *
 * @author Y.Swetake (Modified by Magic Len)
 */
public class QRCodeEncoder {

    //-----類別常數-----
    private static final String QRCODE_DATA_PATH = "/org/magiclen/magicqrcode/data";
    private static final int[] CODE_WORD_NUM_PLUS = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
    private static final int[][] MAX_DATA_BITS_ARRAY = {
	{0, 128, 224, 352, 512, 688, 864, 992, 1232, 1456, 1728, 2032, 2320, 2672, 2920, 3320, 3624, 4056, 4504, 5016, 5352, 5712, 6256, 6880, 7312, 8000, 8496, 9024, 9544, 10136, 10984, 11640, 12328, 13048, 13800, 14496, 15312, 15936, 16816, 17728, 18672},
	{0, 152, 272, 440, 640, 864, 1088, 1248, 1552, 1856, 2192, 2592, 2960, 3424, 3688, 4184, 4712, 5176, 5768, 6360, 6888, 7456, 8048, 8752, 9392, 10208, 10960, 11744, 12248, 13048, 13880, 14744, 15640, 16568, 17528, 18448, 19472, 20528, 21616, 22496, 23648},
	{0, 72, 128, 208, 288, 368, 480, 528, 688, 800, 976, 1120, 1264, 1440, 1576, 1784, 2024, 2264, 2504, 2728, 3080, 3248, 3536, 3712, 4112, 4304, 4768, 5024, 5288, 5608, 5960, 6344, 6760, 7208, 7688, 7888, 8432, 8768, 9136, 9776, 10208},
	{0, 104, 176, 272, 384, 496, 608, 704, 880, 1056, 1232, 1440, 1648, 1952, 2088, 2360, 2600, 2936, 3176, 3560, 3880, 4096, 4544, 4912, 5312, 5744, 6032, 6464, 6968, 7288, 7880, 8264, 8920, 9368, 9848, 10288, 10832, 11408, 12016, 12656, 13328}
    };
    private static final int[] MAX_CODE_WORDS_ARRAY = {0, 26, 44, 70, 100, 134, 172, 196, 242, 292, 346, 404, 466, 532, 581, 655, 733, 815, 901, 991, 1085, 1156, 1258, 1364, 1474, 1588, 1706, 1828, 1921, 2051, 2185, 2323, 2465, 2611, 2761, 2876, 3034, 3196, 3362, 3532, 3706};
    private static final byte[] MATRIX_REMAIN_BIT = {0, 0, 7, 7, 7, 7, 7, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0};
    private static final byte[] FORMAT_INFORMATION_X1 = {0, 1, 2, 3, 4, 5, 7, 8, 8, 8, 8, 8, 8, 8, 8};
    private static final byte[] FORMAT_INFORMATION_Y1 = {8, 8, 8, 8, 8, 8, 8, 8, 7, 5, 4, 3, 2, 1, 0};
    private static final String[] FORMAT_INFORMATION_ARRAY = {"101010000010010", "101000100100101", "101111001111100", "101101101001011", "100010111111001", "100000011001110", "100111110010111", "100101010100000", "111011111000100", "111001011110011", "111110110101010", "111100010011101", "110011000101111", "110001100011000", "110110001000001", "110100101110110", "001011010001001", "001001110111110", "001110011100111", "001100111010000", "000011101100010", "000001001010101", "000110100001100", "000100000111011", "011010101011111", "011000001101000", "011111100110001", "011101000000110", "010010010110100", "010000110000011", "010111011011010", "010101111101101"};

    //-----類別列舉-----
    /**
     * 容錯率。
     */
    public static enum ErrorCorrect {

	NONE, LOW, HIGH, MAX;
    }

    //-----類別方法-----
    private static byte[] calculateRSECC(final byte[] codewords, final byte rsEccCodewords, final byte[] rsBlockOrder, final int maxDataCodewords, final int maxCodewords) throws Exception {
	byte[][] rsCalTableArray = new byte[256][rsEccCodewords];
	try (InputStream fis = QRCodeEncoder.class.getResourceAsStream(String.format("%s/rsc%d.dat", QRCODE_DATA_PATH, rsEccCodewords))) {
	    try (BufferedInputStream bis = new BufferedInputStream(fis)) {
		for (int i = 0; i < 256; i++) {
		    bis.read(rsCalTableArray[i]);
		}
	    }
	}

	/* ---- RS-ECC prepare */
	int i = 0, j = 0;
	int rsBlockNumber = 0;

	final byte[][] rsTemp = new byte[rsBlockOrder.length][];
	final byte res[] = new byte[maxCodewords];
	System.arraycopy(codewords, 0, res, 0, codewords.length);

	while (i < rsBlockOrder.length) {
	    rsTemp[i] = new byte[(rsBlockOrder[i] & 0xFF) - rsEccCodewords];
	    i++;
	}
	i = 0;
	while (i < maxDataCodewords) {
	    rsTemp[rsBlockNumber][j] = codewords[i];
	    j++;
	    if (j >= (rsBlockOrder[rsBlockNumber] & 0xFF) - rsEccCodewords) {
		j = 0;
		rsBlockNumber++;
	    }
	    i++;
	}

	/* ---  RS-ECC main --- */
	rsBlockNumber = 0;
	while (rsBlockNumber < rsBlockOrder.length) {
	    byte[] rsTempData;
	    rsTempData = (byte[]) rsTemp[rsBlockNumber].clone();

	    int rsCodewords = (rsBlockOrder[rsBlockNumber] & 0xFF);
	    int rsDataCodewords = rsCodewords - rsEccCodewords;

	    j = rsDataCodewords;
	    while (j > 0) {
		byte first = rsTempData[0];
		if (first != 0) {
		    byte[] leftChr = new byte[rsTempData.length - 1];
		    System.arraycopy(rsTempData, 1, leftChr, 0, rsTempData.length - 1);
		    byte[] cal = rsCalTableArray[(first & 0xFF)];
		    rsTempData = calculateByteArrayBits(leftChr, cal, "xor");
		} else if (rsEccCodewords < rsTempData.length) {
		    byte[] rsTempNew = new byte[rsTempData.length - 1];
		    System.arraycopy(rsTempData, 1, rsTempNew, 0, rsTempData.length - 1);
		    rsTempData = (byte[]) rsTempNew.clone();
		} else {
		    byte[] rsTempNew = new byte[rsEccCodewords];
		    System.arraycopy(rsTempData, 1, rsTempNew, 0, rsTempData.length - 1);
		    rsTempNew[rsEccCodewords - 1] = 0;
		    rsTempData = (byte[]) rsTempNew.clone();
		}
		j--;
	    }

	    System.arraycopy(rsTempData, 0, res, codewords.length + rsBlockNumber * rsEccCodewords, rsEccCodewords);
	    rsBlockNumber++;
	}
	return res;
    }

    private static byte[] calculateByteArrayBits(final byte[] xa, final byte[] xb, final String ind) {
	int ll, ls;
	byte[] res, xl, xs;

	if (xa.length > xb.length) {
	    xl = (byte[]) xa.clone();
	    xs = (byte[]) xb.clone();
	} else {
	    xl = (byte[]) xb.clone();
	    xs = (byte[]) xa.clone();
	}
	ll = xl.length;
	ls = xs.length;
	res = new byte[ll];

	for (int i = 0; i < ll; i++) {
	    if (i < ls) {
		if (ind.equals("xor")) {
		    res[i] = (byte) (xl[i] ^ xs[i]);
		} else {
		    res[i] = (byte) (xl[i] | xs[i]);
		}
	    } else {
		res[i] = xl[i];
	    }
	}
	return res;
    }

    private static byte[] divideDataBy8Bits(final int[] data, final byte[] bits, final int maxDataCodewords) {
	int l1 = bits.length;
	int l2;
	int codewordsCounter = 0;
	int remainingBits = 8;
	int max = 0;
	int buffer;
	int bufferBits;
	boolean flag;

	if (l1 != data.length) {
	}
	for (int i = 0; i < l1; i++) {
	    max += bits[i];
	}
	l2 = (max - 1) / 8 + 1;
	final byte[] codewords = new byte[maxDataCodewords];
	for (int i = 0; i < l2; i++) {
	    codewords[i] = 0;
	}
	for (int i = 0; i < l1; i++) {
	    buffer = data[i];
	    bufferBits = bits[i];
	    flag = true;

	    if (bufferBits == 0) {
		break;
	    }
	    while (flag) {
		if (remainingBits > bufferBits) {
		    codewords[codewordsCounter] = (byte) ((codewords[codewordsCounter] << bufferBits) | buffer);
		    remainingBits -= bufferBits;
		    flag = false;
		} else {
		    bufferBits -= remainingBits;
		    codewords[codewordsCounter] = (byte) ((codewords[codewordsCounter] << remainingBits) | (buffer >> bufferBits));

		    if (bufferBits == 0) {
			flag = false;
		    } else {
			buffer = (buffer & ((1 << bufferBits) - 1));
			flag = true;
		    }
		    codewordsCounter++;
		    remainingBits = 8;
		}
	    }
	}
	if (remainingBits != 8) {
	    codewords[codewordsCounter] = (byte) (codewords[codewordsCounter] << remainingBits);
	} else {
	    codewordsCounter--;
	}
	if (codewordsCounter < maxDataCodewords - 1) {
	    flag = true;
	    while (codewordsCounter < maxDataCodewords - 1) {
		codewordsCounter++;
		if (flag) {
		    codewords[codewordsCounter] = -20;
		} else {
		    codewords[codewordsCounter] = 17;
		}
		flag = !(flag);
	    }
	}
	return codewords;
    }

    private static byte selectMask(final byte[][] matrixContent, final int maxCodewordsBitWithRemain) {
	final int l = matrixContent.length;
	final int[] d1 = {0, 0, 0, 0, 0, 0, 0, 0};
	final int[] d2 = {0, 0, 0, 0, 0, 0, 0, 0};
	final int[] d3 = {0, 0, 0, 0, 0, 0, 0, 0};
	final int[] d4 = {0, 0, 0, 0, 0, 0, 0, 0};

	int d2And = 0, d2Or = 0;
	final int[] d4Counter = {0, 0, 0, 0, 0, 0, 0, 0};

	for (int y = 0; y < l; y++) {
	    final int[] xData = {0, 0, 0, 0, 0, 0, 0, 0};
	    final int[] yData = {0, 0, 0, 0, 0, 0, 0, 0};
	    final boolean[] xD1Flag = {false, false, false, false, false, false, false, false};
	    final boolean[] yD1Flag = {false, false, false, false, false, false, false, false};

	    for (int x = 0; x < l; x++) {

		if (x > 0 && y > 0) {
		    d2And = matrixContent[x][y] & matrixContent[x - 1][y] & matrixContent[x][y - 1] & matrixContent[x - 1][y - 1] & 0xFF;

		    d2Or = (matrixContent[x][y] & 0xFF) | (matrixContent[x - 1][y] & 0xFF) | (matrixContent[x][y - 1] & 0xFF) | (matrixContent[x - 1][y - 1] & 0xFF);
		}

		for (int maskNumber = 0; maskNumber < 8; maskNumber++) {

		    xData[maskNumber] = ((xData[maskNumber] & 63) << 1) | (((matrixContent[x][y] & 0xFF) >>> maskNumber) & 1);

		    yData[maskNumber] = ((yData[maskNumber] & 63) << 1) | (((matrixContent[y][x] & 0xFF) >>> maskNumber) & 1);

		    if ((matrixContent[x][y] & (1 << maskNumber)) != 0) {
			d4Counter[maskNumber]++;
		    }

		    if (xData[maskNumber] == 93) {
			d3[maskNumber] += 40;
		    }

		    if (yData[maskNumber] == 93) {
			d3[maskNumber] += 40;
		    }

		    if (x > 0 && y > 0) {

			if (((d2And & 1) != 0) || ((d2Or & 1) == 0)) {
			    d2[maskNumber] += 3;
			}

			d2And = d2And >> 1;
			d2Or = d2Or >> 1;
		    }

		    if (((xData[maskNumber] & 0x1F) == 0) || ((xData[maskNumber] & 0x1F) == 0x1F)) {
			if (x > 3) {
			    if (xD1Flag[maskNumber]) {
				d1[maskNumber]++;
			    } else {
				d1[maskNumber] += 3;
				xD1Flag[maskNumber] = true;
			    }
			}
		    } else {
			xD1Flag[maskNumber] = false;
		    }
		    if (((yData[maskNumber] & 0x1F) == 0) || ((yData[maskNumber] & 0x1F) == 0x1F)) {
			if (x > 3) {
			    if (yD1Flag[maskNumber]) {
				d1[maskNumber]++;
			    } else {
				d1[maskNumber] += 3;
				yD1Flag[maskNumber] = true;
			    }
			}
		    } else {
			yD1Flag[maskNumber] = false;
		    }

		}
	    }
	}
	int minValue = 0;
	byte res = 0;
	final int[] d4Value = {90, 80, 70, 60, 50, 40, 30, 20, 10, 0, 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 90};
	for (int maskNumber = 0; maskNumber < 8; maskNumber++) {

	    d4[maskNumber] = d4Value[(int) ((20 * d4Counter[maskNumber]) / maxCodewordsBitWithRemain)];

	    int demerit = d1[maskNumber] + d2[maskNumber] + d3[maskNumber] + d4[maskNumber];

	    if (demerit < minValue || maskNumber == 0) {
		res = (byte) maskNumber;
		minValue = demerit;
	    }
	}
	return res;
    }

    //-----物件變數-----
    private String string = "magiclen.org";
    private ErrorCorrect errorCorrect = ErrorCorrect.HIGH;

    //-----建構子-----
    /**
     * 建構子，不傳入參數，預設的QR Code字串為「magiclen.org」，容錯率為HIGH。
     */
    public QRCodeEncoder() {

    }

    /**
     * 建構子，傳入QR Code字串，容錯率為HIGH。
     *
     * @param string 傳入字串
     */
    public QRCodeEncoder(final String string) {
	if (string != null) {
	    this.setString(string);
	}
    }

    /**
     * 建構子，傳入QR Code字串，和容錯率。
     *
     * @param string 傳入字串
     * @param errorCorrect 傳入容錯率
     */
    public QRCodeEncoder(final String string, final ErrorCorrect errorCorrect) {
	if (string != null) {
	    this.setString(string);
	}
	if (errorCorrect != null) {
	    this.setErrorCorrect(errorCorrect);
	}
    }

    //-----物件方法-----
    /**
     * 進行QR Code的編碼，並傳回結果。
     *
     * @return 傳回QR Code編碼結果
     */
    public boolean[][] encode() {
	try {
	    final byte[] data = string.getBytes("UTF-8");
	    final int dataLength = data.length;

	    if (dataLength == 0) {
		return new boolean[][]{{false}};
	    }

	    final int[] dataValue = new int[dataLength + 32];
	    final byte[] dataBits = new byte[dataLength + 32];
	    final int codewordNumCounterValue;
	    int totalDataBits = 0;
	    int c = 0;
	    dataBits[c] = 4;
	    totalDataBits += 4;
	    dataValue[c] = 4;
	    c++;
	    dataValue[c] = dataLength;

	    dataBits[c] = 8;
	    totalDataBits += 8;

	    codewordNumCounterValue = c++;

	    for (int i = 0; i < dataLength; i++) {
		final int index = i + c;
		dataValue[index] = (data[i] & 0x00FF);
		dataBits[index] = 8;
		totalDataBits += 8;
	    }
	    c += dataLength;

	    final int ec;
	    switch (errorCorrect) {
		case LOW:
		    ec = 0;
		    break;
		case HIGH:
		    ec = 3;
		    break;
		case MAX:
		    ec = 2;
		    break;
		default:
		    ec = 1;
	    }

	    //找出合適的QR Code 大小
	    int maxDataBits = 0;
	    int sizeLevel = 1;
	    for (int i = 1; i <= 40; i++) {
		if ((MAX_DATA_BITS_ARRAY[ec][i]) >= totalDataBits + CODE_WORD_NUM_PLUS[sizeLevel]) {
		    maxDataBits = MAX_DATA_BITS_ARRAY[ec][i];
		    break;
		}
		sizeLevel++;
	    }
	    totalDataBits += CODE_WORD_NUM_PLUS[sizeLevel];
	    dataBits[codewordNumCounterValue] += CODE_WORD_NUM_PLUS[sizeLevel];

	    final int maxCodewords = MAX_CODE_WORDS_ARRAY[sizeLevel];
	    final int byte_num = MATRIX_REMAIN_BIT[sizeLevel] + (maxCodewords << 3);
	    byte[] matrixX = new byte[byte_num];
	    byte[] matrixY = new byte[byte_num];
	    byte[] maskArray = new byte[byte_num];
	    byte[] formatInformationX2 = new byte[15];
	    byte[] formatInformationY2 = new byte[15];
	    byte[] rsEccCodewords = new byte[1];
	    byte[] rsBlockOrderTemp = new byte[128];

	    try (InputStream fis = QRCodeEncoder.class.getResourceAsStream(String.format("%s/qrv%d_%d.dat", QRCODE_DATA_PATH, sizeLevel, ec))) {
		try (BufferedInputStream bis = new BufferedInputStream(fis)) {
		    bis.read(matrixX);
		    bis.read(matrixY);
		    bis.read(maskArray);
		    bis.read(formatInformationX2);
		    bis.read(formatInformationY2);
		    bis.read(rsEccCodewords);
		    bis.read(rsBlockOrderTemp);
		}
	    }

	    byte rsBlockOrderLength = 1;
	    for (byte i = 1; i < 128; i++) {
		if (rsBlockOrderTemp[i] == 0) {
		    rsBlockOrderLength = i;
		    break;
		}
	    }

	    byte[] rsBlockOrder = new byte[rsBlockOrderLength];
	    System.arraycopy(rsBlockOrderTemp, 0, rsBlockOrder, 0, rsBlockOrderLength);

	    int maxDataCodewords = maxDataBits >> 3;

	    int modules1Side = 4 * sizeLevel + 17;
	    int matrixTotalBits = modules1Side * modules1Side;
	    byte[] frameData = new byte[matrixTotalBits + modules1Side];

	    try (InputStream fis = QRCodeEncoder.class.getResourceAsStream(String.format("%s/qrvfr%d.dat", QRCODE_DATA_PATH, sizeLevel))) {
		try (BufferedInputStream bis = new BufferedInputStream(fis)) {
		    bis.read(frameData);
		    bis.close();
		}
	    }

	    if (totalDataBits <= maxDataBits - 4) {
		dataValue[c] = 0;
		dataBits[c] = 4;
	    } else if (totalDataBits < maxDataBits) {
		dataValue[c] = 0;
		dataBits[c] = (byte) (maxDataBits - totalDataBits);
	    } else {
		throw new Exception("Overflow");
	    }
	    final byte[] dataCodewords = divideDataBy8Bits(dataValue, dataBits, maxDataCodewords);
	    final byte[] codewords = calculateRSECC(dataCodewords, rsEccCodewords[0], rsBlockOrder, maxDataCodewords, maxCodewords);

	    final byte[][] matrixContent = new byte[modules1Side][modules1Side];

	    for (int i = 0; i < modules1Side; i++) {
		for (int j = 0; j < modules1Side; j++) {
		    matrixContent[j][i] = 0;
		}
	    }

	    for (int i = 0; i < maxCodewords; i++) {

		byte codeword_i = codewords[i];
		for (int j = 7; j >= 0; j--) {

		    int codewordBitsNumber = (i * 8) + j;

		    matrixContent[matrixX[codewordBitsNumber] & 0xFF][matrixY[codewordBitsNumber] & 0xFF] = (byte) ((255 * (codeword_i & 1)) ^ maskArray[codewordBitsNumber]);

		    codeword_i = (byte) ((codeword_i & 0xFF) >>> 1);
		}
	    }

	    for (int matrixRemain = MATRIX_REMAIN_BIT[sizeLevel]; matrixRemain > 0; matrixRemain--) {
		int remainBitTemp = matrixRemain + (maxCodewords * 8) - 1;
		matrixContent[matrixX[remainBitTemp] & 0xFF][matrixY[remainBitTemp] & 0xFF] = (byte) (255 ^ maskArray[remainBitTemp]);
	    }

	    byte maskNumber = selectMask(matrixContent, MATRIX_REMAIN_BIT[sizeLevel] + maxCodewords * 8);
	    byte maskContent = (byte) (1 << maskNumber);

	    byte formatInformationValue = (byte) (ec << 3 | maskNumber);

	    for (int i = 0; i < 15; i++) {
		byte content = Byte.parseByte(FORMAT_INFORMATION_ARRAY[formatInformationValue].substring(i, i + 1));
		matrixContent[FORMAT_INFORMATION_X1[i] & 0xFF][FORMAT_INFORMATION_Y1[i] & 0xFF] = (byte) (content * 255);
		matrixContent[formatInformationX2[i] & 0xFF][formatInformationY2[i] & 0xFF] = (byte) (content * 255);
	    }
	    final boolean[][] out = new boolean[modules1Side][modules1Side];

	    c = 0;
	    for (int i = 0; i < modules1Side; i++) {
		for (int j = 0; j < modules1Side; j++) {
		    out[j][i] = !((matrixContent[j][i] & maskContent) == 0 && frameData[c] != (char) 49);
		    c++;
		}
		c++;
	    }
	    return out;
	} catch (final Exception ex) {
	    ex.printStackTrace(System.out);
	    return null;
	}
    }

    /**
     * 設定容錯率。
     *
     * @param errorCorrect 傳入容錯率
     */
    public final void setErrorCorrect(final ErrorCorrect errorCorrect) {
	if (errorCorrect != null) {
	    this.errorCorrect = errorCorrect;
	} else {
	    throw new NullPointerException();
	}
    }

    /**
     * 取得容錯率。
     *
     * @return 傳回容錯率
     */
    public ErrorCorrect getErrorCorrect() {
	return this.errorCorrect;
    }

    /**
     * 設定QR Code字串。
     *
     * @param string 傳入QR Code字串
     */
    public final void setString(final String string) {
	if (string != null) {
	    this.string = string;
	} else {
	    throw new NullPointerException();
	}
    }

    /**
     * 取得QR Code字串。
     *
     * @return 傳回QR Code字串
     */
    public String getString() {
	return this.string;
    }
}
