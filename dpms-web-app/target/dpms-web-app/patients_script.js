window.onload = function() {
    fetchPatients();
};

function fetchPatients() {
    fetch('PatientServlet')
        .then(response => response.json())
        .then(data => {
            let tbody = document.querySelector('#data-output tbody');
            tbody.innerHTML = ''; // Clear previous data
            
            data.forEach(patient => {
                let row = document.createElement('tr');
                row.innerHTML = `
                    <td>${patient.patient_id}</td>
                    <td>${patient.first_name}</td>
                    <td>${patient.last_name}</td>
                    <td>${patient.date_of_birth}</td>
                    <td>${patient.gender}</td>
                    <td>${patient.address}</td>
                    <td>${patient.city}</td>
                    <td>${patient.state}</td>
                    <td>${patient.zip_code}</td>
                    <td>${patient.insurance_company}</td>
                    <td>${patient.insurance_number}</td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching patients:', error));
}


