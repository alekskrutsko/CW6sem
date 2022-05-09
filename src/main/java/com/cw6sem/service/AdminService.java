package com.cw6sem.service;

import com.cw6sem.domain.Role;
import com.cw6sem.entity.Appraiser;
import com.cw6sem.entity.User;
import com.cw6sem.repository.AppraisalAgreementRepository;
import com.cw6sem.repository.AppraiserRepository;
import com.cw6sem.repository.UserRepository;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final AppraisalAgreementRepository appraisalAgreementRepository;
    private final AppraiserRepository appraiserRepository;

    public List<User> findAllCustomers(){return userRepository.findByRole(Role.CUSTOMER);}
    public List<User> findAllAppraisers(){return userRepository.findByRole(Role.APPRAISER);}
    public List<Appraiser> findAppraisersFeedback(){
        List<Appraiser> appraiserList = appraiserRepository.findAll();
        List<Appraiser> feedback = new ArrayList<>();
        for (Appraiser appraiserFeedback:appraiserList) {
            if(StringUtils.isNullOrEmpty(appraiserFeedback.getFeedback())) continue;
            feedback.add(appraiserFeedback);
        }
        return feedback;
    }
    public List<User> findCustomersAndAppraisers(){return userRepository.findCustomersAndAppraisers(Role.APPRAISER, Role.CUSTOMER);}
    public void grantAppraiser(Long id){
        User user = userRepository.getById(id);
        user.setRole(Role.APPRAISER);
        userRepository.save(user);
        Appraiser appraiser = new Appraiser();
        appraiser.setUser(user);
        appraiserRepository.save(appraiser);
    }
    public void grantCustomer(Long id){
        User user = userRepository.getById(id);
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);
    }
    public User getUserById(Long id){
        return userRepository.getById(id);
    }


    public List<User> filterCustomersAndAppraisersByStatus(boolean blocked){
        return userRepository.filterCustomersAndAppraisersByStatus(blocked);
    }
    @SneakyThrows
    public List<List<Object>> getChartDataForSixMonth(){
        List<List<Object>> chart = new ArrayList<>();
        int i = 6;
        while(i>0){
            List<Object> res = new ArrayList<>();
            String month="";

            java.util.Date bufDate = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(LocalDate.now().minusMonths(i-1)));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", new Locale("ru"));
            month=simpleDateFormat.format(bufDate);


            res.add(month);
            res.add(appraisalAgreementRepository.lineChart(LocalDate.now().minusMonths(i-1)));

            chart.add(res);
            i--;
        }
        return chart;
    }

    public String getBestAppraisers(){
        List<String> appraisers = appraisalAgreementRepository.getBestAppraisers();
        if(!appraisers.isEmpty()){
            if(appraisers.size() == 1){
                String[] masAppraisers = appraisers.get(0).split(",");
                User user = appraiserRepository.getById(Long.valueOf(masAppraisers[0])).getUser();
                return user.getSurname() + user.getFirstName() + user.getPatronymic() + ", количество заключённых договоров:" + masAppraisers[1];
            }else {
                String[] masAppraisers = appraisers.get(0).split(",");
                String[] masAppraisers1 = appraisers.get(1).split(",");
                User user = appraiserRepository.getById(Long.valueOf(masAppraisers[0])).getUser();
                User user1 = appraiserRepository.getById(Long.valueOf(masAppraisers1[0])).getUser();
                return user.getSurname() + ' ' + user.getFirstName() + ' ' + user.getPatronymic() + ", количество заключённых договоров: " + masAppraisers[1]
                + "#" + user1.getSurname() + ' ' + user1.getFirstName()+ ' ' + user1.getPatronymic() + ", количество заключённых договоров: " + masAppraisers1[1];
            }
        }
        return "Оценщики не найдены";

    }
    @SneakyThrows
    public void createReport(){
        Document document = new Document();

        PdfWriter.getInstance(document, new FileOutputStream("..\\CW6sem\\report.pdf"));

        document.open();
        Font font = FontFactory.getFont("./src/main/resources/static/fonts/NotoSans-Regular.ttf", "cp1251", BaseFont.EMBEDDED, 10);
        Font headerFont = FontFactory.getFont("./src/main/resources/static/fonts/NotoSans-Regular.ttf", "cp1251", BaseFont.EMBEDDED, 14);


        Paragraph header = new Paragraph("ОТЧЕТ ПО ДЕЯТЕЛЬНОСТИ КОМПАНИИ ПО ОЦЕНКЕ ОБЪЕКТОВ НЕДВИЖИМОСТИ", headerFont);
        header.add(new Paragraph(" "));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);



        LocalDate currentDate = LocalDate.now();
        java.util.Date current = null;

        current = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(currentDate));


        String currentDateStr = DateFormat.getDateInstance(SimpleDateFormat.LONG, new Locale("ru")).format(current);
        Paragraph created = new Paragraph("Сгенерирован " + currentDateStr, font);
        created.add(new Paragraph(" "));
        created.setAlignment(Element.ALIGN_CENTER);
        document.add(created);



        Paragraph header1 = new Paragraph("1. СТАТИСТИКА ПО ДОХОДАМ ЗА ПОЛУГОДИЕ", headerFont);
        header1.add(new Paragraph(" "));
        header1.setAlignment(Element.ALIGN_CENTER);
        document.add(header1);

        PdfPTable table = new PdfPTable(2);

        PdfPCell cell1 = new PdfPCell(new Paragraph("Месяц", font));
        PdfPCell cell2 = new PdfPCell(new Paragraph("Прибыль, руб.", font));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell1);
        table.addCell(cell2);

        List<List<Object>> chartData = getChartDataForSixMonth();

        for (List<Object> data: chartData){
            PdfPCell bufCell1 = new PdfPCell(new Paragraph((String) data.get(0), font));
            PdfPCell bufCell2 = new PdfPCell(new Paragraph(String.valueOf(data.get(1)), font));
            bufCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            bufCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(bufCell1);
            table.addCell(bufCell2);
        }

        document.add(table);
        document.add(new Paragraph("\n"));


        Paragraph header2 = new Paragraph("2. СТАТИСТИКА ОЦЕНЩИКОВ КОМПАНИИ", headerFont);
        header2.add(new Paragraph(" "));
        header2.setAlignment(Element.ALIGN_CENTER);
        document.add(header2);

        PdfPTable table3 = new PdfPTable(2);

        cell1 = new PdfPCell(new Paragraph("Оценщик, ФИО", font));
        cell2 = new PdfPCell(new Paragraph("Количество успешно заключенных договоров, шт.", font));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

        table3.addCell(cell1);
        table3.addCell(cell2);


        List<String> appraisers = appraisalAgreementRepository.getBestAppraisers();

        for (String appraiser : appraisers) {
            String[] masAppraiser = appraiser.split(",");
            User user = appraiserRepository.getById(Long.valueOf(masAppraiser[0])).getUser();
            PdfPCell bufCell1 = new PdfPCell(new Paragraph(user.getSurname() + " " +
                    user.getFirstName() + " " + user.getPatronymic(), font));
            PdfPCell bufCell2 = new PdfPCell(new Paragraph(masAppraiser[1], font));
            bufCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            bufCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(bufCell1);
            table3.addCell(bufCell2);
        }
        document.add(table3);
        document.close();

    }

}
