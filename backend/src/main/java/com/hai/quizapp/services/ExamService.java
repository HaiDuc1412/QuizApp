package com.hai.quizapp.services;

import com.hai.quizapp.dtos.exam.ExamResultDTO;
import com.hai.quizapp.dtos.exam.ExamSubmitRequestDTO;

public interface ExamService {

    ExamResultDTO submitExam(ExamSubmitRequestDTO requestDTO);
}
