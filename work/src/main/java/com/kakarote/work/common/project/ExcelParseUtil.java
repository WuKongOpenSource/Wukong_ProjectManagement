package com.kakarote.work.common.project;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.utils.UserUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExcelParseUtil {

    private static final ThreadLocal<Closeable> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 统一导出数据模板
     */
    public static void exportExcel(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list, HttpServletResponse response, Integer isXls) {
        exportExcel(dataList, excelParseService, list, response, isXls, true);
    }

    /**
     * 统一导出数据模板
     *
     * @param isClose 是否关闭writer流 true为关闭
     */
    public static void exportExcel(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list, HttpServletResponse response, Integer isXls, boolean isClose) {
        String key = UserUtil.getUserId().toString();
        File file = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + key + ".data");
        String fileName;
        try {
            if (isXls == 1) {
                fileName = URLEncoder.encode(excelParseService.getExcelName() + "信息", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : "");
                exportExcelData(dataList, excelParseService,isXls, list, file);
            } else {
                fileName = URLEncoder.encode(excelParseService.getExcelName() + "信息" + ".csv", "UTF-8");
                exportExcelCsv(dataList, excelParseService, list, file);
            }
            if (!isClose) {
                List<? extends Map<String, Object>> nextData = excelParseService.getNextData();
                if (nextData != null && !nextData.isEmpty()) {
                    exportExcel(nextData, excelParseService, list, response, isXls, false);
                } else {
                    isClose = true;
                }
            }
            if (isClose) {
                if (isXls == 1) {
                    //自定义标题别名
                    //response为HttpServletResponse对象
                    response.setContentType("application/vnd.ms-excel;charset=utf-8");
                    response.setCharacterEncoding("UTF-8");
                    //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                    response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                    Closeable closeable = THREAD_LOCAL.get();
                    if(ObjectUtil.isNotEmpty(closeable)){
                        ((ExcelWriter)closeable).flush();
                    }
                    FileUtil.writeToStream(file, response.getOutputStream());
                } else {
                    response.setContentType("application/csv;charset=utf-8");
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                    //解决csv文件乱码问题
                    response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                    FileUtil.writeToStream(file, response.getOutputStream());
                }
            }
        } catch (Exception ex) {
            isClose = true;
            log.error("导出数据错误", ex);
        } finally {
            if (isClose) {
                IoUtil.close(THREAD_LOCAL.get());
                THREAD_LOCAL.remove();
                FileUtil.del(file);
            }
        }

    }


    /**
     * 统一导出数据模板
     */
    public static void exportExcelData(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService,int isXls, List<?> list, File file) {
        try {
            ExcelWriter writer;
            boolean isInit = false;
            if (THREAD_LOCAL.get() == null) {
                writer = ObjectUtil.isNotEmpty(isXls) && 1== isXls ? ExcelUtil.getBigWriter(file) : ExcelUtil.getWriter();
                THREAD_LOCAL.set(writer);
                isInit = true;
            } else {
                writer = (ExcelWriter) THREAD_LOCAL.get();
            }
            List<ExcelDataEntity> headList = excelParseService.parseData(list, false);
            Map<String, Integer> headMap = new HashMap<>(headList.size(), 1.0f);
            for (ExcelDataEntity head : headList) {
                if (isInit) {
                    writer.addHeaderAlias(head.getFieldName(), head.getName());
                }
                if (!Arrays.asList(FieldEnum.AREA.getType(), FieldEnum.DETAIL_TABLE.getType()).contains(head.getType())) {
                    headMap.put(head.getFieldName(), head.getType());
                }
            }
            if (isInit) {
                // 取消数据的黑色边框以及数据左对齐
                CellStyle cellStyle = writer.getCellStyle();
                cellStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyle.setBorderTop(BorderStyle.NONE);
                cellStyle.setBorderBottom(BorderStyle.NONE);
                cellStyle.setBorderLeft(BorderStyle.NONE);
                cellStyle.setBorderRight(BorderStyle.NONE);
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                Font defaultFont = writer.createFont();
                defaultFont.setFontHeightInPoints((short) 11);
                cellStyle.setFont(defaultFont);
                // 取消数字格式的数据的黑色边框以及数据左对齐
                CellStyle cellStyleForNumber = writer.getStyleSet().getCellStyleForNumber();
                cellStyleForNumber.setBorderTop(BorderStyle.NONE);
                cellStyleForNumber.setBorderBottom(BorderStyle.NONE);
                cellStyleForNumber.setBorderLeft(BorderStyle.NONE);
                cellStyleForNumber.setBorderRight(BorderStyle.NONE);
                cellStyleForNumber.setAlignment(HorizontalAlignment.LEFT);
                cellStyleForNumber.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyleForNumber.setFont(defaultFont);
                // 取消日期格式的数据的黑色边框以及数据左对齐
                CellStyle cellStyleForDate = writer.getStyleSet().getCellStyleForDate();
                cellStyleForDate.setBorderTop(BorderStyle.NONE);
                cellStyleForDate.setBorderBottom(BorderStyle.NONE);
                cellStyleForDate.setBorderLeft(BorderStyle.NONE);
                cellStyleForDate.setBorderRight(BorderStyle.NONE);
                cellStyleForDate.setAlignment(HorizontalAlignment.LEFT);
                cellStyleForDate.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyleForDate.setFont(defaultFont);
                //设置行高以及列宽
                writer.setRowHeight(-1, 20);
                writer.setColumnWidth(-1, 20);
                //只保留别名中的字段
                writer.setOnlyAlias(true);
            }
            // 设置数据
            formatData(dataList, excelParseService, headMap);
            if (dataList.isEmpty()) {
                Map<String, Object> record = new HashMap<>();
                headList.forEach(head -> record.put(head.getFieldName(), ""));
                writer.write(Collections.singletonList(record), isInit);
            } else {
                writer.write(dataList, isInit);
            }
            CellStyle style = writer.getHeadCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);
            Font font = writer.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);
        } catch (Exception e) {
            log.error("导出客户错误：", e);
        }
    }


    /**
     * 统一导出数据模板
     */
    public static void exportExcelCsv(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, List<?> list, File file) {
        CsvWriter writer;
        boolean isInit = false;
        if (THREAD_LOCAL.get() == null) {
            writer = CsvUtil.getWriter(file, Charset.defaultCharset());
            THREAD_LOCAL.set(writer);
            isInit = true;
        } else {
            writer = (CsvWriter) THREAD_LOCAL.get();
        }
        List<ExcelDataEntity> headList = excelParseService.parseData(list, false);
        Map<String, Integer> headMap = new HashMap<>(headList.size(), 1.0f);
        List<Object> names = new ArrayList<>();
        List<String> fieldNames = new LinkedList<>();
        for (ExcelDataEntity head : headList) {
            if (isInit) {
                names.add(head.getName());
            }
            fieldNames.add(head.getFieldName());
            if (!Arrays.asList(FieldEnum.AREA.getType(), FieldEnum.CURRENT_POSITION.getType(), FieldEnum.DETAIL_TABLE.getType()).contains(head.getType())) {
                headMap.put(head.getFieldName(), head.getType());
            }
        }
        List<List<Object>> writerList = new ArrayList<>();
        if (!names.isEmpty()) {
            writerList.add(names);
        }
        // 设置数据
        formatData(dataList, excelParseService, headMap);
        for (Map<String, Object> data : dataList) {
            List<Object> values = new ArrayList<>();
            for (String fieldName : fieldNames) {
                values.add(data.get(fieldName));
            }
            writerList.add(values);
        }
        writer.write(writerList);
    }


    public static void formatData(List<? extends Map<String, Object>> dataList, ExcelParseService excelParseService, Map<String, Integer> headMap) {
        for (Map<String, Object> record : dataList) {
            excelParseService.getFunc().call(record, headMap);
        }
    }


    private static final int TWO = 2;
    /**
     * 统一下载导入模板
     */
    public static void importExcel(ExcelParseService excelParseService, List<?> list, HttpServletResponse response, String module) {
        List<ExcelDataEntity> dataEntities = excelParseService.parseData(list, true);
        try (ExcelWriter writer = ExcelUtil.getWriter(excelParseService.isXlsx())) {
            //因为重复合并单元格会导致样式丢失，所以先获取全部字段一次合并
            int sum = dataEntities.stream().mapToInt(data -> excelParseService.addCell(null, 0, 0, data.getFieldName())).sum();
            int addressSum = dataEntities.stream().filter(e -> ObjectUtil.equal(e.getType(),FieldEnum.AREA_POSITION.getType())).mapToInt(e ->{
                switch (e.getPrecisions()){
                    case 1:
                        return 3;
                    case 2:
                        return 2;
                    case 3:
                        return 1;
                    case 4:
                    default:
                        return 0;
                }
            }).sum();
            sum += addressSum;
            writer.renameSheet(excelParseService.getExcelName() + "导入模板");
            writer.merge(dataEntities.size() - 1 + sum, excelParseService.getMergeContent(module), true);
            writer.getHeadCellStyle().setAlignment(HorizontalAlignment.LEFT);
            writer.getHeadCellStyle().setWrapText(true);
            Font headFont = writer.createFont();
            headFont.setFontHeightInPoints((short) 11);
            writer.getHeadCellStyle().setFont(headFont);
            writer.getHeadCellStyle().setFillPattern(FillPatternType.NO_FILL);
            if (!"subject".equals(module)) {
                writer.getOrCreateRow(0).setHeightInPoints(120);
            } else {
                writer.getOrCreateRow(0).setHeightInPoints(60);
            }
            writer.setRowHeight(-1, 20);
            //单选按钮字符超过255时的sheet的index
            Integer sheetIndex = 0;
            int crmFieldIndex = 0;
            //设置样式
            for (int i = 0, k = dataEntities.size(), z = 0; i < k; i++, z++) {
                ExcelDataEntity dataEntity = dataEntities.get(i);
                //会新增cell或者对当前cell做调整，直接跳过默认处理
                int n = excelParseService.addCell(writer, z, 1, dataEntity.getFieldName());
                if (n > 0) {
                    z += n;
                    continue;
                }
                CellStyle columnStyle = writer.getOrCreateColumnStyle(z);
                //设置统一字体
                columnStyle.setFont(headFont);
                DataFormat dateFormat = writer.getWorkbook().createDataFormat();
                if (Objects.equals(dataEntities.get(i).getType(), FieldEnum.DATE.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATE_PATTERN));
                } else if (Objects.equals(dataEntities.get(i).getType(), FieldEnum.DATETIME.getType())) {
                    columnStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATETIME_PATTERN));
                } else {
                    columnStyle.setDataFormat(dateFormat.getFormat("@"));
                }
                writer.setColumnWidth(z, 20);
                Cell cell = writer.getOrCreateCell(z, 1);
                //必填字段的特殊处理
                if (Objects.equals(1, dataEntity.getIsNull())) {
                    cell.setCellValue("*" + dataEntity.getName());
                    CellStyle cellStyle = writer.getOrCreateCellStyle(z, 1);
                    Font cellFont = writer.createFont();
                    cellFont.setFontHeightInPoints((short) 11);
                    cellFont.setColor(Font.COLOR_RED);
                    cellStyle.setFont(cellFont);
                    cell.setCellStyle(cellStyle);
                } else {
                    cell.setCellValue(dataEntity.getName());
                }
                if (FieldEnum.ATTENTION.getType().equals(dataEntity.getType())) {
                    dataEntity.setSetting(Arrays.asList("一星", "二星", "三星", "四星", "五星"));
                }
                //选择类型增加下拉框
                if (CollUtil.isNotEmpty(dataEntity.getSetting()) && !ObjectUtil.equal(dataEntity.getType(),FieldEnum.CHECKBOX.getType())) {
                    String[] array = dataEntity.getSetting().stream().map(data -> {
                        if (data instanceof JSONObject && ((JSONObject) data).containsKey("name")) {
                            return ((JSONObject) data).getString("name");
                        }
                        return data.toString();
                    }).toArray(String[]::new);
                    CellRangeAddressList addressList = new CellRangeAddressList(2, 10002, z, z);
                    // array的字符总长度
                    int arrayLength = Arrays.stream(array).mapToInt(String::length).sum();
                    //中文字符串占用长度和ASCII长度不一致，所以此处不判断为255
                    if (arrayLength >= 120) {
                        sheetIndex++;
                        Workbook workbook = writer.getWorkbook();
                        String sheetName = "setting" + sheetIndex;
                        //将下拉框数据放到新的sheet里，然后excel通过新的sheet数据加载下拉框数据
                        Sheet sheet = workbook.createSheet(sheetName);

                        // 创建名称，可被其他单元格引用
                        Name namedCell = workbook.createName();
                        namedCell.setNameName(sheetName);
                        namedCell.setRefersToFormula(sheetName+"!$A$1:$A$" + 1000);

                        //创建单元格对象
                        Cell cellSetting =null;
                        //遍历我们上面的数组，将数据取出来放到新sheet的单元格中
                        for (int l = 0, length = array.length; l < length; l++) {
                            //取出数组中的每个元素
                            String name = array[l];
                            //根据i创建相应的行对象（说明我们将会把每个元素单独放一行）
                            Row row = sheet.createRow(l);
                            //创建每一行中的第一个单元格
                            cellSetting = row.createCell(0);
                            //然后将数组中的元素赋值给这个单元格
                            cellSetting.setCellValue(name);
                        }
                        //加载数据,将sheet中的数据转换为List形式
                        DVConstraint constraint = DVConstraint.createFormulaListConstraint(sheetName);
                        // 将设置下拉选的位置和数据的对应关系 绑定到一起
                        DataValidation dataValidation = new HSSFDataValidation(addressList, constraint);
                        //将当前sheet设置为隐藏
                        workbook.setSheetHidden(sheetIndex, true);
                        //将数据赋给下拉列表
                        workbook.getSheetAt(0).addValidationData(dataValidation);
                    } else {
                        writer.addSelect(addressList, array);
                    }
                } else if (FieldEnum.ATTENTION.getType().equals(dataEntity.getType())) {
                    writer.addSelect(new CellRangeAddressList(2, 10002, z, z), "一星", "二星", "三星", "四星", "五星");
                }


                // 省市区
                if(ObjectUtil.equal(dataEntity.getType(),FieldEnum.AREA_POSITION.getType())) {
                    crmFieldIndex++;
                    Workbook wb = writer.getWorkbook();
                    Sheet sheet = writer.getSheet();
                    String fieldName = dataEntity.fieldName;
                    int four = 4;
                    for (int j = 0; j < four; j++) {
                        writer.setColumnWidth(z + j, 20);
                    }
                    Sheet hideSheet = wb.createSheet(fieldName);
                    wb.setSheetHidden(wb.getSheetIndex(hideSheet), true);
                    int rowId = 0;
                    // 这个addressIndex是需要在省市区结束后给excel的x向右移动几位
                    int addressIndex = 0;

                    if(dataEntity.getPrecisions() <= 4){
                        Cell cell1 = writer.getOrCreateCell(z, 1);
                        if (Objects.equals(1, dataEntity.getIsNull())) {
                            cell1.setCellValue("*" + dataEntity.getName() + "-省");
                            CellStyle cellStyle = writer.getOrCreateCellStyle(z, 1);
                            Font cellFont = writer.createFont();
                            cellFont.setFontHeightInPoints((short) 11);
                            cellFont.setColor(Font.COLOR_RED);
                            cellStyle.setFont(cellFont);
                            cell1.setCellStyle(cellStyle);
                        }else{
                            cell1.setCellValue(dataEntity.getName() + "-省");
                        }
                    }
                    if(dataEntity.getPrecisions() <= 3){
                        Cell cell2 = writer.getOrCreateCell(z + 1, 1);
                        cell2.setCellValue(dataEntity.getName() + "-市");
                        addressIndex++;
                    }
                    if(dataEntity.getPrecisions() <= 2){
                        Cell cell3 = writer.getOrCreateCell(z + 2, 1);
                        cell3.setCellValue(dataEntity.getName() + "-区");
                        addressIndex++;
                    }
                    if(dataEntity.getPrecisions() <= 1){
                        Cell cell4 = writer.getOrCreateCell(z + 3, 1);
                        cell4.setCellValue(dataEntity.getName() + "-详细地址");
                        addressIndex++;
                    }
                    // 设置第一行，存省的信息
                    Row provinceRow = hideSheet.createRow(rowId++);
                    provinceRow.createCell(0).setCellValue(dataEntity.getName() +  "-省列表");
                    String[] provinceList = CrmExcelUtil.getProvinceArray();
                    for (int line = 0; line < provinceList.length; line++) {
                        Cell provinceCell = provinceRow.createCell(line + 1);
                        provinceCell.setCellValue(provinceList[line]);
                    }

                    // 只执行一次
                    if(crmFieldIndex == 1){
                        // 将具体的数据写入到每一行中，行开头为父级区域，后面是子区域。
                        Map<String, List<String>> areaMap = CrmExcelUtil.getAreaMap();
                        for (String key : areaMap.keySet()) {
                            List<String> son = areaMap.get(key);
                            Row subRow = hideSheet.createRow(rowId++);
                            subRow.createCell(0).setCellValue(key);
                            for (int line = 0; line < son.size(); line++) {
                                Cell cellTemp = subRow.createCell(line + 1);
                                cellTemp.setCellValue(son.get(line));
                            }
                            // 添加名称管理器
                            String range = CrmExcelUtil.getRange(1, rowId, son.size());
                            Name name = wb.createName();
                            // key不可重复
                            name.setNameName(key);
                            String formula = fieldName + "!" + range;
                            name.setRefersToFormula(formula);
                        }
                    }

                    // 省级下拉框
                    CellRangeAddressList provRangeAddressList = new CellRangeAddressList(2, 10004, z, z);
                    DataValidationHelper validationHelper = sheet.getDataValidationHelper();
                    DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(provinceList);
                    //设置下拉框数据
                    DataValidation dataValidation = validationHelper.createValidation(constraint, provRangeAddressList);
                    dataValidation.createErrorBox("error", "请选择正确的省份");
                    sheet.addValidationData(dataValidation);

                    //市 区下拉框
                    int forIndex = 10004;
                    for (int line = TWO; line < forIndex; line++) {
                        if (dataEntity.getPrecisions() <= 3) {
                            CrmExcelUtil.setDataValidation(CrmExcelUtil.getCorrespondingLabel(z + 1), sheet, line, z + 1);
                        }
                        if (dataEntity.getPrecisions() <= 2) {
                            CrmExcelUtil.setDataValidation(CrmExcelUtil.getCorrespondingLabel(z + 2), sheet, line, z + 2);
                        }
                    }

                    z += addressIndex;
                }
            }
            //自定义标题别名
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(excelParseService.getExcelName() + "导入模板", "utf-8") + ".xls" + (excelParseService.isXlsx() ? "x" : ""));
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            log.error("下载" + excelParseService.getExcelName() + "导入模板错误", e);
        }

    }

    @Data
    public static class ExcelDataEntity {

        /* 字段名称 */
        private String fieldName;

        /* 展示名称 */
        private String name;

        /* 字段类型 */
        private Integer type;

        /* 是否必填 1 是 0 否 */
        private Integer isNull;

        /* 设置列表 */
        private List<Object> setting;

        /* 精度，允许的最大小数位/地图精度/明细表格、逻辑表单展示方式 */
        private Integer precisions;

        public ExcelDataEntity() {
        }

        public ExcelDataEntity(String fieldName, String name, Integer type) {
            this.fieldName = fieldName;
            this.name = name;
            this.type = type;
        }
    }

    public static abstract class ExcelParseService {

        /**
         * 设置自定义数据处理方法
         *
         * @return func
         */
        public DataFunc getFunc() {
            return (record, headMap) -> {
            };
        }

        /**
         * 统一处理数据
         *
         * @param list        请求头数据
         * @param importExcel 是否是导入模板
         * @return 转化后的请求头数据
         */
        public List<ExcelDataEntity> parseData(List<?> list, boolean importExcel) {
            List<ExcelDataEntity> entities = list.stream().map(obj -> {
                if (obj instanceof ExcelDataEntity) {
                    return (ExcelDataEntity) obj;
                }
                return BeanUtil.copyProperties(obj, ExcelDataEntity.class);
            }).collect(Collectors.toList());
            if (importExcel) {
                entities.removeIf(head -> ExcelParseUtil.removeFieldByType(head.getType()));
            } else {
                entities.removeIf(head -> FieldEnum.HANDWRITING_SIGN.getType().equals(head.getType()));
            }
            entities.stream().forEach(data->{
                if(ObjectUtil.equal(data.getType(),FieldEnum.TAG.getType())){
                    data.setSetting(new ArrayList<>());
                }
            });
            return entities;
        }

        /**
         * 如果需要执行全部数据导出，分批次的获取数据
         *
         * @return data
         */
        public List<? extends Map<String, Object>> getNextData() {
            return null;
        }

        /**
         * 获取excel表格名称
         *
         * @return 表格名称
         */
        public abstract String getExcelName();

        /**
         * 导入的时候需要的可能需要新增字段场景
         *
         * @param writer    writer
         * @param x         – X坐标，从0计数，即列号
         * @param y         – Y坐标，从0计数，即行号
         * @param fieldName 字段名称
         * @return 新增的行数
         */
        public int addCell(ExcelWriter writer, Integer x, Integer y, String fieldName) {
            return 0;
        }

        /**
         * 是否是xlsx格式，xlsx导出会比xlx3倍左右，谨慎使用
         *
         * @return isXlsx
         */
        public boolean isXlsx() {
            return false;
        }

        public String getMergeContent(String module) {
            if ("user".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、手机号：目前只支持中国大陆的11位手机号码；且手机号不允许重复\n" +
                        "3、登录密码：密码由6-20位字母、数字组成\n" +
                        "4、部门：上下级部门间用\"/\"隔开，且从最上级部门开始，例如“上海分公司/市场部/市场一部”。如出现相同的部门，则默认导入组织架构中顺序靠前的部门\n";
            }
            else if (ObjectUtil.equal("activity", module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、跟进时间：推荐格式为2020-2-1\n" +
                        "3、若相关数据有多条时用“/”区分例如：杭州科技有限公司/卡卡罗特软件科技有限公司\n" +
                        "4、所属XX中的'XX'需要存在系统中，且填写的所属名称与系统中的名称必须保持一致否则会导入失败\n" +
                        "5、创建人为系统员工，请填写系统员工“姓名”，若匹配不到系统员工，则会导致导入失败\n" +
                        "6、如果系统中存在多个名称重复的情况，会默认导入到最新的数据中";
            }
            else if ("finance".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、凭证字要与系统保持一致\n" +
                        "3、同一天同一个凭证的凭证号要保持一致\n" +
                        "4、科目编码要与系统保持一致\n" +
                        "5、日期：推荐格式为2020-02-02";
            }
            else if ("subject".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、若导入的为一级科目，则上级科目编号填‘0’\n"+
                        "3、多辅助核算以“/”隔开";
            }
            else if ("achievement".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、业绩目标只能填写数字\n" +
                        "3、年份只填数字 例：2021";
            }
            else if ("marketing".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、日期时间：推荐格式为2020-02-02 13:13:13\n" +
                        "3、日期：推荐格式为2020-02-02\n" +
                        "4、手机号：支持6-15位数字（包含国外手机号格式）\n" +
                        "5、邮箱：只支持邮箱格式\n" +
                        "6、多行文本：字数限制为800字\n" +
                        "7、参与人员：多个参与人员用逗号隔开";
            }
            else if ("productCategory".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、产品类别编号不允许重复，重复则会导入失败\n" +
                        "3、产品类别父类编号为产品类别上一级类别\n" +
                        "4、若无产品类别父类编号则为一级分类\n" +
                        "5、产品类别最多设置20级";
            }
            else if ("fieldCheckIn".equals(module)) {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、拜访客户的名称需要存在系统中，且填写的拜访客户与系统中的名称必须保持一致否则会导入失败\n" +
                        "3、创建人为系统员工，请填写系统员工“姓名”，若匹配不到系统员工，则会导致导入失败\n" +
                        "4、如果系统中存在多个名称重复的情况，会默认导入到最新的数据中";
            }
            else {
                return "注意事项：\n" +
                        "1、表头标“*”的红色字体为必填项\n" +
                        "2、日期时间：推荐格式为2020-02-02 13:13:13\n" +
                        "3、日期：推荐格式为2020-02-02\n" +
                        "4、手机号：支持6-15位数字（包含国外手机号格式）\n" +
                        "5、邮箱：只支持邮箱格式\n" +
                        "6、多行文本：字数限制为800字\n" +
                        "7、标签字段：如果需要导入多个标签，标签之间用英文逗号隔开";
            }
        }


    }

    /**
     * 数据格式化方法
     */
    @FunctionalInterface
    public interface DataFunc {
        /**
         * 数据格式化方法
         *
         * @param record  记录
         * @param headMap 标题
         */
        void call(Map<String, Object> record, Map<String, Integer> headMap);
    }

    /**
     * 不支持导入的字段
     */
    private static final List<Integer> TYPE_LIST = Arrays.asList(FieldEnum.FILE.getType(), FieldEnum.USER.getType(), FieldEnum.STRUCTURE.getType(),
            FieldEnum.AREA.getType(), FieldEnum.CURRENT_POSITION.getType(), FieldEnum.DATE_INTERVAL.getType(), FieldEnum.BOOLEAN_VALUE.getType(),
            FieldEnum.HANDWRITING_SIGN.getType(), FieldEnum.DESC_TEXT.getType(), FieldEnum.DETAIL_TABLE.getType(), FieldEnum.CALCULATION_FUNCTION.getType(),
            FieldEnum.FIELD_GROUP.getType(), FieldEnum.SERIAL_NUMBER.getType(), FieldEnum.ATTENTION.getType()
    );


    /**
     * 删除不支持导入的字段
     *
     * @return true为不支持导入
     */
    public static boolean removeFieldByType(Integer type) {
        return TYPE_LIST.contains(type);
    }

    public static ExcelDataEntity toEntity(String fieldName, String name) {
        return new ExcelDataEntity(fieldName, name, FieldEnum.TEXT.getType());
    }

    public static ExcelDataEntity toEntity(String fieldName, String name, Integer type) {
        return new ExcelDataEntity(fieldName, name, type);
    }
}
