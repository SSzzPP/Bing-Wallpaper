package com.wdbyte.bing.html;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wdbyte.bing.BingFileUtils;
import com.wdbyte.bing.Images;
import com.wdbyte.bing.html.HtmlConstant.Head;
import com.wdbyte.bing.html.HtmlConstant.ImgCard;
import com.wdbyte.bing.html.HtmlConstant.MonthHistory;
import com.wdbyte.bing.html.HtmlConstant.Sidebar;

public class WebSiteGenerator {

    public static void main(String[] args) throws IOException {
        List<Images> bingImages = BingFileUtils.readBing();
        bingImages = bingImages.stream().filter(img -> img.getUrl() != null).collect(Collectors.toList());
        Map<String, List<Images>> monthMap = BingFileUtils.convertImgListToMonthMap(bingImages);
        WebSiteGenerator generator = new WebSiteGenerator();
        generator.htmlGeneratorIndex(bingImages, monthMap);
        generator.htmlGeneratorMonth(monthMap);
    }

    public void htmlGenerator() throws IOException {
        List<Images> bingImages = BingFileUtils.readBing();
        bingImages = bingImages.stream().filter(img -> img.getUrl() != null).collect(Collectors.toList());
        Map<String, List<Images>> monthMap = BingFileUtils.convertImgListToMonthMap(bingImages);
        htmlGeneratorIndex(bingImages, monthMap);
        htmlGeneratorMonth(monthMap);
    }

    public void htmlGeneratorIndex(List<Images> bingImages, Map<String, List<Images>> monthMap) throws IOException {
        String templateFile = HtmlFileUtils.readIndexTemplateFile();
        // Replace header image and description
        String indexHtml = replaceHead(templateFile, bingImages.get(0), null);
        // Replace side catalog
        indexHtml = replaceSidebar(indexHtml, monthMap, null);
        // Replace Picture List
        indexHtml = replaceImgList(indexHtml, bingImages.subList(0, 30));
        // Replace Bottom Monthly History
        indexHtml = replaceMonthHistory(indexHtml, monthMap, null);
        // Write to file
        HtmlFileUtils.writeIndexHtml(indexHtml);
    }

    public void htmlGeneratorMonth(Map<String, List<Images>> monthMap) throws IOException {
        for (String month : monthMap.keySet()) {
            List<Images> bingImages = monthMap.get(month);
            String templateFile = HtmlFileUtils.readIndexTemplateFile();
            // Replace header image and description
            String html = replaceHead(templateFile, bingImages.get(0), month);
            // Replace side catalog
            html = replaceSidebar(html, monthMap, month);
            // Replace Picture List
            html = replaceImgList(html, bingImages);
            // Replace Bottom Monthly History
            html = replaceMonthHistory(html, monthMap, month);
            // Write to file
            HtmlFileUtils.writeMonthHtml(month, html);
        }
    }

    public String replaceSidebar(String html, Map<String, List<Images>> monthMap, String nowMonth) {
        StringBuilder sidebar = new StringBuilder();
        for (String month : monthMap.keySet()) {
            String sidabarMenu = Sidebar.getSidabarMenuList(month + ".html", month);
            if (month != null && month.equals(nowMonth)) {
                sidabarMenu = sidabarMenu.replace(Sidebar.VAR_SIDABAR_COLOR, Sidebar.VAR_SIDABAR_NOW_COLOR);
            }
            sidebar.append(sidabarMenu);
        }
        return html.replace(Sidebar.VAR_SIDABAR, sidebar.toString());
    }

    /**
     * Updated large header image and description
     *
     * @param html
     * @param images
     * @param month
     * @return
     */
    public String replaceHead(String html, Images images, String month) {
        html = html.replace(Head.HEAD_IMG_URL, images.getUrl());
        html = html.replace(Head.HEAD_IMG_DESC, images.getDesc());
        if (month != null) {
            html = html.replace(Head.HEAD_TITLE, "Bing Wallpaper(" + month + ")");
        } else {
            html = html.replace(Head.HEAD_TITLE, "Bing Wallpaper");
        }
        return html;
    }

    public String replaceImgList(String html, List<Images> bingImages) {
        StringBuilder imgList = new StringBuilder();
        for (Images bingImage : bingImages) {
            imgList.append(ImgCard.getImgCard(bingImage.getUrl(), bingImage.getDate()));
        }
        return html.replace(ImgCard.VAR_IMG_CARD_LIST, imgList.toString());
    }

    /**
     * Replace the monthly history link at the bottom
     *
     * @param monthMap
     * @param nowMonth
     * @return
     */
    public String replaceMonthHistory(String html, Map<String, List<Images>> monthMap, String nowMonth) {
        StringBuilder monthHistory = new StringBuilder();
        for (String month : monthMap.keySet()) {
            String history = MonthHistory.getMonthHistory(month + ".html", month);
            if (month != null && month.equals(nowMonth)) {
                history = history.replace(MonthHistory.VAR_MONTH_HISTORY_MONTH_COLOR, MonthHistory.VAR_MONTH_HISTORY_NOW_MONTH_COLOR);
            }
            monthHistory.append(history + " ");
        }
        return html.replace(MonthHistory.VAR_MONTH_HISTORY, monthHistory.toString());
    }

}
