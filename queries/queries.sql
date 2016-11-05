select gender, count(0) from employees group by gender;

select emp_no, count(0), max(salary) from salaries group by emp_no order by max(salary) desc limit 2;

SELECT
  d.dept_name, 
  max(s.salary) 
FROM
  employees e 
  LEFT JOIN current_dept_emp cde
    ON cde.emp_no = e.emp_no 
  LEFT JOIN (select emp_no, max(salary) as salary from salaries group by emp_no) s
    ON s.emp_no = e.emp_no 
  LEFT JOIN departments d
    ON d.dept_no = cde.dept_no 
GROUP BY
  d.dept_name 
ORDER BY
  max(s.salary) DESC
LIMIT
  2;
