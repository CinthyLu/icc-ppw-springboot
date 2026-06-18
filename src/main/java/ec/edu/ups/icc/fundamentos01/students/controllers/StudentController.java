package ec.edu.ups.icc.fundamentos01.students.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ec.edu.ups.icc.fundamentos01.students.models.Student;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/students") // Es la ruta base y tambein se pueden colocar las versiones

public class StudentController {
    private List<Student> students = new ArrayList<>();

    public StudentController() {
        students.add(new Student(1L, "Cinthya", 27));
        students.add(new Student(2L, "Catalina", 16));
        students.add(new Student(3L, "Paul", 23));
    }

    @GetMapping() // get
    public List<Student> getAllStudents() {
        return students;
    }

    @GetMapping("/count")
    public String getCount() {
        return "Total Estudiantes: " + students.size();
    }

}