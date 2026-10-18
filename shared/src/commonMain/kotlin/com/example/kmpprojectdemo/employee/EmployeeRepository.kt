package com.example.kmpprojectdemo.employee

class EmployeeRepository(
    private val remoteDataSource: EmployeeRemoteDataSource = EmployeeRemoteDataSource()
) {
    suspend fun getEmployee(): Result<Employee> {
        return runCatching {
            val dto = remoteDataSource.fetchEmployee()
            val fullName = "${dto.firstName} ${dto.lastName}".trim()
            val dept = dto.company?.department ?: "General"
            Employee(
                id = dto.id,
                fullName = fullName,
                email = dto.email,
                phone = dto.phone,
                department = dept
            )
        }
    }
}
