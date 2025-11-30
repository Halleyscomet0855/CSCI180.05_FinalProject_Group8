package app.components;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.entities.Class;
import app.entities.ClassEntry;
import app.entities.Student;
import app.repositories.ClassEntryRepository;
import app.repositories.ClassRepository;

@Component
public class ProfessorComponent {

    private final ClassRepository classRepository;
    private final ClassEntryRepository classEntryRepository;

    // Define a threshold for "cutting" class
    private static final int CUT_THRESHOLD = 3; 

    @Autowired
    public ProfessorComponent(ClassRepository classRepository, ClassEntryRepository classEntryRepository) {
        this.classRepository = classRepository;
        this.classEntryRepository = classEntryRepository;
    }

    /**
     * Generates a report of students who have "cut" (accumulated too many lates) a specific class.
     * @param classId The primary key of the class.
     * @return A formatted string report of students who have cut the class.
     * @throws IllegalArgumentException if the class is not found.
     */
    public String generateCutReport(Long classId) {
        Optional<Class> classOpt = classRepository.findById(classId);
        if (!classOpt.isPresent()) {
            throw new IllegalArgumentException("Class not found with ID: " + classId);
        }
        Class classEntity = classOpt.get();

        // Find all ClassEntry objects for this class
        List<ClassEntry> classEntries = classEntryRepository.findByClassPK(classEntity);

        // Filter students who have exceeded the cut threshold for lates
        List<Student> studentsWhoCut = classEntries.stream()
            .filter(entry -> entry.getNumberOfLate() >= CUT_THRESHOLD)
            .map(ClassEntry::getStudentPK)
            .collect(Collectors.toList());

        // Build the report string
        StringBuilder report = new StringBuilder();
        report.append("Cut Report for Class: ").append(classEntity.getClassName()).append("\n");
        report.append("Professor: ").append(classEntity.getProfessorName()).append("\n");
        report.append("---------------------------------------\n");

        if (studentsWhoCut.isEmpty()) {
            report.append("No students have cut this class.\n");
        } else {
            report.append("Students who have cut this class (").append(CUT_THRESHOLD).append(" or more lates):\n");
            for (Student student : studentsWhoCut) {
                // Find the specific ClassEntry to get the exact late count for the student in this class
                Optional<ClassEntry> studentClassEntry = classEntries.stream()
                    .filter(entry -> entry.getStudentPK().equals(student))
                    .findFirst();
                
                String lateCountInfo = studentClassEntry.map(entry -> "(" + entry.getNumberOfLate() + " lates)").orElse("");
                report.append("- ").append(student.getName()).append(" (ID: ").append(student.getIDNumber()).append(") ").append(lateCountInfo).append("\n");
            }
        }
        report.append("---------------------------------------\n");
        
        return report.toString();
    }
    
    // You can add more Professor-specific methods here, e.g., viewAttendance()
}