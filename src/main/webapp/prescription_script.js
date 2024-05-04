// Deontae Cocroft | 2024 May 4th
window.onload = function() {
    
    // Fetches table information upon window opening
    fetchPrescriptionBills();
    fetchPrescriptions();

    // Add bill
    document.getElementById('addPreBillForm').addEventListener('submit', addPreBill);

    // Delete
    document.getElementById('deletePreBillForm').addEventListener('submit', deletePreBill);
    document.getElementById('clearPreDeleteButton').addEventListener('click',clearDeleteField);
    document.getElementById('deletePreBillButton').addEventListener('click', showDeletePrompt);
    document.getElementById('cancelPreDeleteButton').addEventListener('click', cancelDeletePrompt);

    // Search
    document.getElementById('searchPreBillForm').addEventListener('submit', searchPreBill);
    document.getElementById('clearPreSearchButton').addEventListener('click',clearSearchField);
    document.getElementById('searchPreBillButton').addEventListener('click', showSearchPrompt);
    document.getElementById('cancelPreSearchButton').addEventListener('click', cancelSearchPrompt);

    // PDF
    document.getElementById('pdfPreBillForm').addEventListener('submit', pdfPreBill);
    document.getElementById('clearPrePdfButton').addEventListener('click', clearPdfField);
    document.getElementById('generatePrePdfButton').addEventListener('click', showPdfPrompt);
    document.getElementById('cancelPrePdfButton').addEventListener('click', cancelPdfPrompt);

    // Clear Fields
    document.getElementById('clearFieldsButton').addEventListener('click', clearFields);

    // Main Menu
    document.getElementById('mainMenuButton').addEventListener('click', mainMenu);

    // Patient
    document.getElementById('patientPreButton').addEventListener('click', patientsWindow);

    // Help
    document.getElementById('helpPreButton').addEventListener('click', helpPrompt); 
};

// Clear 
function clearFields() {
    document.getElementById('addPreBillForm').reset();
}

// Search
function clearSearchField() {
    document.getElementById('searchPreBillForm').reset();
}

function showSearchPrompt() {
    document.getElementById('searchPrePrompt').style.display = 'block';
}

function cancelSearchPrompt() {
    document.getElementById('searchPrePrompt').style.display = 'none';
}

// Delete
function clearDeleteField() {
    document.getElementById('deletePreBillForm').reset();
    
}

function cancelDeletePrompt() {
    document.getElementById('deletePrePrompt').style.display = 'none';
}

function showDeletePrompt() {
    document.getElementById('deletePrePrompt').style.display = 'block';
}

// PDF
function clearPdfField() {
    document.getElementById('pdfPreBillForm').reset();
}

function showPdfPrompt() {
    document.getElementById('pdfPrePrompt').style.display = 'block';
}

function cancelPdfPrompt() {
    document.getElementById('pdfPrePrompt').style.display = 'none';
}

// Main Menu
function mainMenu(){
    window.location.href = 'main_menu.html';
}

// Patients Window
function patientsWindow(){
    window.location.href = 'patients.html';
}

// Help -
function helpPrompt(){
    alert("To search Prescription Bill click button and only use Patient ID or Prescription Bill ID Fields."+ 
    "\n\nTo delete Prescription Bill click button and only use Prescription Bill ID field."+ 
    "\n\nTo save pdf click generate pdf button and enter prescription bill id"+
    "\n\nEnsure all fields marked with * are filled out. Ensure numerical value for Patient ID, Prescription ID, Quantity, and Prescription Bill ID.",)
}

// Function that fetches Prescription bill information for html table
function fetchPrescriptionBills(){

    // Fetch Prescription billing information
    fetch('prescriptionBilling')
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        if (response.status === 204) {
            return [];
        }
        return response.json();
    })
    .then(data => {
        const tbody = document.querySelector('#prescriptionBillTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table
        data.forEach(prescriptionBill => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${prescriptionBill.prescriptionBillId}</td>
                    <td>${prescriptionBill.patientId}</td>
                    <td>${prescriptionBill.prescriptionId}</td>
                    <td>${prescriptionBill.quantity}</td>
                    <td>${prescriptionBill.total}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching bills:', error);
    });
}

// Function that fetches Prescription information for html table
function fetchPrescriptions(){

    // Fetch Prescription information
    fetch('prescription')
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        if (response.status === 204) {
            return [];
        }
        return response.json();
    })
    .then(data => {
        const tbody = document.querySelector('#prescriptionTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table
        data.forEach(prescriptionBill => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${prescriptionBill.prescriptionId}</td>
                    <td>${prescriptionBill.name}</td>
                    <td>${prescriptionBill.notes}</td>
                    <td>${prescriptionBill.price}</td>
                    <td>${prescriptionBill.itemsInStock}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching bills:', error);
    });

}

function searchPreBill(event){
    event.preventDefault();

    // Getting the values from the fields that will be used to search prescription bills.
    const prescriptionBillId = document.getElementById('searchPreBillId').value;
    const patientId = document.getElementById('patientId').value;

    // Fetches method to search prescription bills by prescription bill id and patient id
    fetch(`searchPreBill?prescriptionBillId=${prescriptionBillId}&patientId=${patientId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const tbody = document.querySelector('#prescriptionBillTable tbody');
            tbody.innerHTML = '';
            
            // Layout of table for receive information from java and send it back to html table
            data.forEach(prescriptionBill => {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${prescriptionBill.prescriptionBillId}</td>
                        <td>${prescriptionBill.patientId}</td>
                        <td>${prescriptionBill.prescriptionId}</td>
                        <td>${prescriptionBill.quantity}</td>
                        <td>${prescriptionBill.total}</td>
                 
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching bills:', error);
        });
}

// Function that allows users to add new prescription bills.
function addPreBill(event){
    event.preventDefault();

    const form = new FormData(document.getElementById('addPreBillForm'));

    // Fetch POST java method to take information entered in HTML and add prescription bill to the database
    fetch('addPreBill', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams(form).toString()
    })

    .then(response => {
        if (!response.ok) {
            alert('Patient id or prescription id does not exist. Also, make sure prescription bill does not put stock below 0.');
            throw new Error('Network response was not ok: ' + response.statusText);
            
        }
        // Updates table, sends message, and resets the addPreBillForm after prescription bill is added.
        fetchPrescriptionBills();
        fetchPrescriptions();
        alert('Prescription bill added successfully.');
        document.getElementById('addPreBillForm').reset();
    })
    .catch(error => {
        console.error('Error adding prescription bill:', error);
        alert('Error adding prescription bill. Please try again.');
    });

}

// Function that deletes prescription bills from database.
function deletePreBill(event){
    event.preventDefault();

    // Get the value of prescription bill id to delete.
    const prescriptionBillId = document.getElementById('deletePreBillId').value;
    

    // Calls java DELETE method and sends prescription bill id value so java can remove prescription bill.
    fetch(`deletePreBill?prescriptionBillId=${prescriptionBillId}`, {
        method: 'DELETE',
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Provides message, updates table, resets form, and closes delete prompt after bill is deleted successfully.
        alert('Prescription Bill Id: ' + prescriptionBillId + ' deleted successfully or did not exist.');
        fetchPrescriptionBills();
        fetchPrescriptions();
        document.getElementById('deletePrePrompt').style.display = 'none';
        document.getElementById('deletePreBillForm').reset();
    })
    .catch(error => {
        console.error('Error deleting prescription bill:', error);
        alert('Error deleting prescription bill. Please try again.');
    });

}

// Function to create pdf for prescription bill
function pdfPreBill(event){
    event.preventDefault();
        // Get the prescription bill ID entered by the user
        const prescriptionBillId = document.getElementById('pdfPreBillId').value;
    
        // Send a POST request to the servlet to generate the PDF
        fetch('pdfPreBill', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'prescriptionBillId=' + encodeURIComponent(prescriptionBillId)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error generating PDF: ' + response.statusText);
            }
            return response.blob();
        })
        .then(blob => {
            // Create a link element to trigger download
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = 'Prescription_bill_' + prescriptionBillId + '.pdf';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        })
        .catch(error => {
            console.error('Error generating PDF:', error);
            alert('Error generating PDF. Prescription Bill ID: ' + prescriptionBillId + ' might not exist. Please try again.');
        });
}
