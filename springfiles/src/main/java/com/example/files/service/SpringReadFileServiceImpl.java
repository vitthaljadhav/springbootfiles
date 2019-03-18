package com.example.files.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.files.entities.User;
import com.example.files.repos.SpringReadFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
@Transactional
public class SpringReadFileServiceImpl implements SpringReadFileService {

	@Autowired
	private SpringReadFileRepository fileRepository;

	@Override
	public List<User> findAll() {
		return fileRepository.findAll();
	}

	@Override
	public boolean saveDataFromUploadFile(MultipartFile file) {
		boolean isFlag = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("json")) {
			isFlag = readDataFromJson(file);
		} else if (extension.equalsIgnoreCase("csv")) {
			isFlag = readDataFromCsv(file);
		} else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
			isFlag = readDataFromExcel(file);
		}
		return false;
	}

	private boolean readDataFromExcel(MultipartFile file) {
		Workbook workbook = getWorkbook(file);

		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();

		rows.next();
		while (rows.hasNext()) {
			Row row = rows.next();
			User user = new User();
			if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setFirstname(row.getCell(0).getStringCellValue());
			
			} else if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setLastname(row.getCell(1).getStringCellValue());
			
			} else if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setEmail(row.getCell(2).getStringCellValue());
			
			} else if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
				String phonenumber = NumberToTextConverter.toText(row.getCell(3).getNumericCellValue());
				user.setPhonenumber(phonenumber);
			
			} /*else if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
				user.setPhonenumber(row.getCell(3).getStringCellValue());
			}*/
			user.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
			fileRepository.save(user);
		}

		return false;
	}

	private Workbook getWorkbook(MultipartFile file) {
		Workbook workbook = null;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		try {
			if (extension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(file.getInputStream());
			} else if (extension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(file.getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}
	private boolean readDataFromCsv(MultipartFile file) {
		try {
			InputStreamReader reader = new InputStreamReader(file.getInputStream());
			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			List<String[]> rows = csvReader.readAll();
			for (String[] row : rows) {
				fileRepository.save(new User(row[0], row[1], row[2], row[3],
						FilenameUtils.getExtension(file.getOriginalFilename())));
			}
			return true;

		} catch (IOException e) {
			return false;
		}
	}

	private boolean readDataFromJson(MultipartFile file) {
		try {
			InputStream inputStream = file.getInputStream();

			ObjectMapper objectMapper = new ObjectMapper();
			List<User> users = Arrays.asList(objectMapper.readValue(inputStream, User[].class));
			if (users != null && users.size() > 0) {
				for (User user : users) {
					user.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
					fileRepository.save(user);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
