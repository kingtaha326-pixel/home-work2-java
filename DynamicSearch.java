import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

// 1. الواجهات (Interfaces)
interface Taxable {
    double TAX_RATE = 0.15;
    double calculateTax();
}

interface BonusCalculable {
    double calculateBonus(double score);
}

// 2. الفئة المجردة الأساسية (Employee)
abstract class Employee implements Taxable, BonusCalculable, Comparable<Employee> {
    private String id;
    private String name;
    protected double baseSalary;
    private LocalDate joinDate;

    public Employee(String id, String name, double baseSalary, LocalDate joinDate) {
        this.id = id;
        this.name = name;
        this.baseSalary = baseSalary;
        this.joinDate = joinDate;
    }

    public abstract double calculateNetSalary();

    @Override
    public double calculateTax() {
        return baseSalary * TAX_RATE; //[span_2](start_span)[span_2](end_span)
    }

    @Override
    public int compareTo(Employee other) {
        // ترتيب تنازلي حسب صافي الراتب (الأعلى دخلاً إلى الأقل)[span_3](start_span)[span_3](end_span)
        return Double.compare(other.calculateNetSalary(), this.calculateNetSalary());
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getBaseSalary() { return baseSalary; }
    public LocalDate getJoinDate() { return joinDate; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Base Salary: " + baseSalary + 
               ", Join Date: " + joinDate + ", Net Salary: " + calculateNetSalary();
    }
}

// 3. الفئة الفرعية للموظف الكامل (FullTimeEmployee)
class FullTimeEmployee extends Employee {
    private double allowance;

    public FullTimeEmployee(String id, String name, double baseSalary, LocalDate joinDate, double allowance) {
        super(id, name, baseSalary, joinDate);
        this.allowance = allowance;
    }

    @Override
    public double calculateNetSalary() {
        return baseSalary + allowance - calculateTax(); //[span_4](start_span)[span_4](end_span)
    }

    @Override
    public double calculateBonus(double score) {
        // المكافأة بنسبة 10% من الراتب الأساسي مضروبة في درجة التقييم[span_5](start_span)[span_5](end_span)
        return (baseSalary * 0.10) * score;
    }
}

// 4. الفئة الفرعية للموظف المتعاقد (ContractorEmployee)
class ContractorEmployee extends Employee {
    private int hourlyRate;
    private int hoursWorked;

    public ContractorEmployee(String id, String name, LocalDate joinDate, int hourlyRate, int hoursWorked) {
        super(id, name, 0.0, joinDate); // الراتب الأساسي مثبت بـ 0[span_6](start_span)[span_6](end_span)
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateNetSalary() {
        double grossSalary = hourlyRate * hoursWorked; //[span_7](start_span)[span_7](end_span)
        return grossSalary - calculateTax();
    }

    @Override
    public double calculateBonus(double score) {
        // تمنح مكافأة مقطوعة بقيمة 500$ فقط إذا كان تقييم الأداء أعلى من 4.0[span_8](start_span)[span_8](end_span)
        if (score > 4.0) {
            return 500.0;
        }
        return 0.0;
    }
}

// 5. الفئة الرئيسية لتنفيذ النظام مع الإدخال اليدوي (PayrollSystem)
public class PayrollSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Employee> employees = new ArrayList<>();

        System.out.println("=== نظام إدارة الرواتب والإنتاجية (Payroll Management System) ===");

        // حلقة تكرار لإدخال البيانات يدوياً
        while (true) {
            System.out.println("\nاختر نوع الموظف المراد إضافته:");
            System.out.println("1. موظف دوام كامل (Full-Time Employee)");
            System.out.println("2. موظف متعاقد (Contractor Employee)");
            System.out.println("3. إنهاء الإدخال وعرض التقارير والفرز");
            System.out.print("الرجاء إدخال الخيار (1 أو 2 أو 3): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // تنظيف الـ Buffer

            if (choice == 1) {
                System.out.print("أدخل المعرف (ID): ");
                String id = scanner.nextLine();
                System.out.print("أدخل الاسم: ");
                String name = scanner.nextLine();
                System.out.print("أدخل الراتب الأساسي: ");
                double baseSalary = scanner.nextDouble();
                scanner.nextLine();
                System.out.print("أدخل تاريخ الانضمام (بالتنسيق YYYY-MM-DD): ");
                LocalDate joinDate = LocalDate.parse(scanner.nextLine());
                System.out.print("أدخل البدل (Allowance): ");
                double allowance = scanner.nextDouble();
                scanner.nextLine();

                employees.add(new FullTimeEmployee(id, name, baseSalary, joinDate, allowance));
                System.out.println("✅ تم إضافة موظف الدوام الكامل بنجاح!");

            } else if (choice == 2) {
                System.out.print("أدخل المعرف (ID): ");
                String id = scanner.nextLine();
                System.out.print("أدخل الاسم: ");
                String name = scanner.nextLine();
                System.out.print("أدخل تاريخ الانضمام (بالتنسيق YYYY-MM-DD): ");
                LocalDate joinDate = LocalDate.parse(scanner.nextLine());
                System.out.print("أدخل أجر الساعة (Hourly Rate): ");
                int hourlyRate = scanner.nextInt();
                System.out.print("أدخل عدد ساعات العمل (Hours Worked): ");
                int hoursWorked = scanner.nextInt();
                scanner.nextLine();

                employees.add(new ContractorEmployee(id, name, joinDate, hourlyRate, hoursWorked));
                System.out.println("✅ تم إضافة الموظف المتعاقد بنجاح!");

            } else if (choice == 3) {
                if (employees.isEmpty()) {
                    System.out.println("⚠️ القائمة فارغة! يرجى إدخال موظف واحد على الأقل قبل الإنهاء.");
                    continue;
                }
                break;
            } else {
                System.out.println("❌ خيار غير صحيح، حاول مرة أخرى.");
            }
        }

        scanner.close();

        // عرض البيانات المدخلة قبل الترتيب
        System.out.println("\n=== 📊 الموظفون قبل الترتيب ===");
        for (Employee e : employees) {
            System.out.println(e);
        }

        // 2. فرز القائمة باستعمال Collections.sort() بناءً على Comparable (صافي الراتب تنازلياً)[span_9](start_span)[span_9](end_span)
        Collections.sort(employees);
        System.out.println("\n=== 🔄 الموظفون بعد الفرز باستخدام Comparable (حسب صافي الراتب تنازلياً) ===");
        for (Employee e : employees) {
            System.out.println(e);
        }

        // 3. إعادة فرز القائمة باستخدام Comparator مخصص للاعتماد على تاريخ الانضمام (الأقدمية)[span_10](start_span)[span_10](end_span)
        employees.sort(Comparator.comparing(Employee::getJoinDate));
        System.out.println("\n=== 📅 الموظفون بعد إعادة الفرز باستخدام Comparator (حسب تاريخ الانضمام - الأقدمية) ===");
        for (Employee e : employees) {
            System.out.println(e);
        }

        // 4. حساب التكلفة الإجمالية للضرائب والمكافآت الموزعة عبر التكرار الموحد (Polymorphism)[span_11](start_span)[span_11](end_span)
        double totalTax = 0;
        double totalBonus = 0;
        double performanceScoreExample = 4.5; // درجة تقييم افتراضية لاختبار حساب المكافآت

        for (Employee e : employees) {
            totalTax += e.calculateTax();
            totalBonus += e.calculateBonus(performanceScoreExample);
        }

        System.out.println("\n=== 💰 التقارير المالية والإدارية ===");
        System.out.println("إجمالي الضرائب المحصلة: " + totalTax + " $");
        System.out.println("إجمالي المكافآت الموزعة (بناءً على تقييم " + performanceScoreExample + "): " + totalBonus + " $");
    }
}
