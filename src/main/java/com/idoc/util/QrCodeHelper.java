package com.idoc.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
//import com.xiaoying.qiniuapi.ApiQiniu;


public final class QrCodeHelper {
    private QrCodeHelper() {}
    /**
     * 编码（将文本生成二维码）
    *
    * @param content 二维码中的内容
    * @param width 二维码图片宽度
    * @param height 二维码图片高度
    * @param imagePath 二维码图片存放位置
    * @return 图片地址
    */
   public static String encode(String content, int width, int height, String imagePath) {
    Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
    // 设置编码类型为utf-8
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    // 设置二维码纠错能力级别为H（最高）
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    BitMatrix byteMatrix = null;
    try {
     // 生成二维码
     byteMatrix = new MultiFormatWriter().encode(content,
       BarcodeFormat.QR_CODE, width, height, hints);
     File file = new File(imagePath);
     MatrixToImageWriter.writeToFile(byteMatrix, "png", file);
    } catch (IOException e) {
     e.printStackTrace();
    } catch (WriterException e) {
     e.printStackTrace();
    }

    return imagePath;
   }
   /**
    * 解码（读取二维码图片中的文本信息）
    * @param imagePath 二维码图片路径
    * @return 文本信息
    */
   public static String decode(String imagePath) {
    // 返回的文本信息
    String content = "";
    try {
     // 创建图片文件
     File file = new File(imagePath);
     if (!file.exists()) {
      return content;
     }
     BufferedImage image = null;
     image = ImageIO.read(file);
     if (null == image) {
      return content;
     }
     // 解码
     LuminanceSource source = new BufferedImageLuminanceSource(image);
     BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
     Hashtable hints = new Hashtable();
     hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
     Result rs = new MultiFormatReader().decode(bitmap, hints);
     content = rs.getText();
    } catch (IOException e) {
     e.printStackTrace();
    } catch (ReaderException e) {
     e.printStackTrace();
    }
    return content;
   }
   /**
    * 图片打水印
    * @param bgImage 背景图
    * @param waterImg 水印图
    * @param uniqueFlag 生成的新图片名称中的唯一标识，用来保证生成的图片名称不重复，如果为空或为null,将使用当前时间作为标识
    * @return 新图片路径
    */
   public static String addImageWater(String bgImage, String waterImg ,String uniqueFlag) {
    int x = 0;
    int y = 0;
    String newImgPath = "";

    if(null == uniqueFlag){
     uniqueFlag = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }else if(uniqueFlag.trim().length() < 1){
     uniqueFlag = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    try {
     File file = new File(bgImage);
     String fileName = file.getName();
     Image image = ImageIO.read(file);
     int width = image.getWidth(null);
     int height = image.getHeight(null);
     BufferedImage bufferedImage = new BufferedImage(width, height,
       BufferedImage.TYPE_INT_RGB);
     Graphics2D g = bufferedImage.createGraphics();
     g.drawImage(image, 0, 0, width, height, null);
     Image waterImage = ImageIO.read(new File(waterImg)); // 水印文件
     int width_water = waterImage.getWidth(null);
     int height_water = waterImage.getHeight(null);
     g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
       1));
     int widthDiff = width - width_water;
     int heightDiff = height - height_water;
     x = widthDiff / 2;
     y = heightDiff / 2;

     g.drawImage(waterImage, x, y, width_water, height_water, null); // 水印文件结束
     g.dispose();



     if(bgImage.contains(fileName)){
      newImgPath = bgImage.replace(fileName, uniqueFlag+fileName);
     }
     File newImg = new File(newImgPath);
     ImageIO.write(bufferedImage, "png", newImg);

     File waterFile = new File(waterImg);

     if(file.exists()){
      file.delete();
     }

     if(waterFile.exists()){
      waterFile.delete();
     }
    } catch (IOException e) {
     e.printStackTrace();
    }

    return newImgPath;
   }
   /**
     * 图片缩放
    * @param filePath 图片路径
    * @param height 缩放到高度
    * @param width 缩放宽度
    * @param fill 比例足时是否填白 true为填白，二维码是黑白色，这里调用时建议设为true
    * @return 新图片路径
    */
   public static String resizeImg(String filePath, int width,int height, boolean fill) {

    String newImgPath = "";

    try {
     double ratio = 0; // 缩放比例
     File f = new File(filePath);
     String fileName = f.getName();
     BufferedImage bi = ImageIO.read(f);
     Image itemp = bi.getScaledInstance(width, height,BufferedImage.SCALE_SMOOTH);

     if(height != 0 && width!= 0 ){
      // 计算比例
      if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
       if (bi.getHeight() > bi.getWidth()) {
        ratio = (new Integer(height)).doubleValue()
          / bi.getHeight();
       } else {
        ratio = (new Integer(width)).doubleValue() / bi.getWidth();
       }
       AffineTransformOp op = new AffineTransformOp(AffineTransform
         .getScaleInstance(ratio, ratio), null);
       itemp = op.filter(bi, null);
      }
     }

     if (fill) {
      BufferedImage image = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
      Graphics2D g = image.createGraphics();
      g.setColor(Color.white);
      g.fillRect(0, 0, width, height);
      if (width == itemp.getWidth(null)){
       g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
         itemp.getWidth(null), itemp.getHeight(null),
         Color.white, null);
      }else{
       g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
         itemp.getWidth(null), itemp.getHeight(null),
         Color.white, null);
      }
      g.dispose();
      itemp = image;
     }
     String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
     if(filePath.contains(fileName)){
      newImgPath = filePath.replace(fileName, now+fileName);
     }

     File newImg = new File(newImgPath);
     ImageIO.write((BufferedImage)itemp, "png", newImg);
    } catch (IOException e) {
     e.printStackTrace();
    }

    return newImgPath;
   }


   /**
    * 图片添加边框
    * @param mainImgPath 要加边框的图片
    * @param bgImgPath 背景图（实际上是将图片放在背景图上，只利用背景图的边框效果）
    * @return 制作完成的图片路径
    */
   public static String addWaterBorder(String mainImgPath,String bgImgPath){

    String borderImgPath = "";

    try {
     File f = new File(mainImgPath);

     BufferedImage bi;

     bi = ImageIO.read(f);

     //背景图长宽都比主图多4像素，这是因为我画的背景图的边框效果的大小正好是4像素，
     //主图周边比背景图少4像素正好能把背景图的边框效果完美显示出来
     int width = bi.getWidth();
     int height = bi.getHeight();

     int bgWidth = width+4;
     int bgHeight = height+4;

     String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
     borderImgPath = QrCodeHelper.addImageWater(QrCodeHelper.resizeImg(bgImgPath, bgHeight, bgWidth, true), mainImgPath,now);

     if(f.exists()){
      f.delete();
     }
    } catch (IOException e) {
     e.printStackTrace();
    }


    return borderImgPath;
   }

   public static void main(String[] args) {

       /**部分一开始***********生成常规二维码*************/
       //二维码内容
//       String content = "https://cardloan.xiaoying.com/kadai/index#!/login";
//           String content = "https://www.baidu.com";
//    String content = "http://ygi9z.free.netapp.cc/";
    String content = "我工作了";
    //二维码宽度
       int width = 700;
       //二维码高度
       int height = 700;
       //二维码存放地址
//       String imagePath = "/Users/cuijianglin/Desktop/卡贷/qr/image/generate1.png";
           String imagePath = "d:/image/3.png";
    //生成二维码,返回的是生成好的二维码图片的所在路径
       String qrImgPath = QrCodeHelper.encode(content, width, height, imagePath);
       /**部分一结束***********如果生成不带图片的二维码，到这步已经完成了*************/


//       /**部分二开始***********如果生成带图片但图片不带边框的二维码，解开这部分注释*************/
//       //缩放水印图片,为保证二维码的读取正确，图片不超过二维码图片的五分之一，这里设为六分之一
//       String waterImgPath = QrCodeHelper.resizeImg("/Users/cuijianglin/Desktop/卡贷/qr/image/center.jpg", width/6, height/6, true);
//
//       //生成带有图片的二维码，返回的是生成好的二维码图片的所在路径
//      String qrImage = QrCodeHelper.addImageWater(qrImgPath, waterImgPath,"qr");
//       /**部分二结束***********如果生成带图片但图片不带边框的二维码，解开这部分注释*************/


       /**部分三开始（部分三不能和部分二共存）***********如果生成带图片且图片带边框的二维码，解开这部分注释****/

       //缩放水印图片,为保证二维码的读取正确，图片不超过二维码图片的五分之一，这里设为六分之一
       //d:/qr/heihei.png 这图片是要加在二维码中间的那张图
     //  String waterImgPath = QRUtil.resizeImg("d:/qr/heihei.png", width/6, height/6, true);
     //
     //  //d:/qr/qr_bg.png这种图片是自己画好边框光晕效果的边框底图
     //  String tempImg = QRUtil.addWaterBorder(waterImgPath, "d:/qr/qr_bg.png");
     //
     //  //生成带有边框图片的二维码,返回的是生成好的二维码图片的所在路径
     //  String qrImage = QRUtil.addImageWater(qrImgPath, tempImg,"thatway");
       /**部分三结束***********如果生成带图片且图片带边框的二维码，解开这部分注释*************/



       /*******测试一下解码******/
//      System.out.println(QrCodeHelper.decode(qrImage));;

      }
    /**
     * 生成二维码链接
     * @param url
     * @param qrPath
     * @param fileNamePre
     * @param mode
     * @param centerImagePath
     * @return
     */
//    public static String generateQrUrl(String url, String qrPath,String fileNamePre,String mode,String centerImagePath)
//    {
//        String content = url;
//        //二维码宽度
//        int width = 390;
//        //二维码高度
//        int height = 390;
//        //二维码存放地址
//        String imagePath = qrPath+"/qr.png";
//        //生成二维码,返回的是生成好的二维码图片的所在路径
//        String qrImgPath = QrCodeHelper.encode(content, width, height, imagePath);
//        /**部分一结束***********如果生成不带图片的二维码，到这步已经完成了*************/
//        /**部分二开始***********如果生成带图片但图片不带边框的二维码，解开这部分注释*************/
//        //缩放水印图片,为保证二维码的读取正确，图片不超过二维码图片的五分之一，这里设为六分之一
//        String waterImgPath = QrCodeHelper.resizeImg(centerImagePath, width/6, height/6, true);
//        //生成带有图片的二维码，返回的是生成好的二维码图片的所在路径
//        String qrImage = QrCodeHelper.addImageWater(qrImgPath, waterImgPath,fileNamePre);
//        /**部分二结束***********如果生成带图片但图片不带边框的二维码，解开这部分注释*************/
//       /**
//        * 上传到七牛
//        */
//       File newImg = new File(qrImage);
//       ApiQiniu qiniu = ApiQiniu.getInstance();
//       qiniu.setMode(mode);
//       String qrCodeUrl = qiniu.upload_file(newImg, "image");
//       if (StringUtils.isEmpty(qrCodeUrl)) {
//           return null;
//       }
//       //删除文件
//       if(newImg.exists())
//       {
//           newImg.delete();
//       }
//       return qiniu.getPrivateUrl(qrCodeUrl);
//    }

}
