// Deontae Cocroft | 2024 May 4th
window.onload = function() {
    
    // Fetches table information upon window opening
    fetchBills();
    
    // Add bill
    document.getElementById('addBillForm').addEventListener('submit', addBill);

    // Delete
    document.getElementById('deleteBillForm').addEventListener('submit', deleteBill);
    document.getElementById('clearDeleteButton').addEventListener('click',clearDeleteField);
    document.getElementById('deleteBillButton').addEventListener('click', showDeletePrompt);
    document.getElementById('cancelDeleteButton').addEventListener('click', cancelDeletePrompt);

    // Search
    document.getElementById('searchBillForm').addEventListener('submit', searchBill);
    document.getElementById('clearSearchButton').addEventListener('click',clearSearchField);
    document.getElementById('searchBillButton').addEventListener('click', showSearchPrompt);
    document.getElementById('cancelSearchButton').addEventListener('click', cancelSearchPrompt);

    // Mark
    document.getElementById('markBillForm').addEventListener('submit', markBill);
    document.getElementById('clearMarkButton').addEventListener('click', clearMarkField);
    document.getElementById('isPaidBillButton').addEventListener('click', showMarkPrompt);
    document.getElementById('cancelMarkButton').addEventListener('click', cancelMarkPrompt);

    // PDF
    document.getElementById('pdfBillForm').addEventListener('submit', pdfBill);
    document.getElementById('clearPdfButton').addEventListener('click', clearPdfField);
    document.getElementById('generatePdfButton').addEventListener('click', showPdfPrompt);
    document.getElementById('cancelPdfButton').addEventListener('click', cancelPdfPrompt);

    // Clear Fields
    document.getElementById('clearFieldsButton').addEventListener('click', clearFields);

    // Main Menu
    document.getElementById('mainMenuButton').addEventListener('click', mainMenu);

    // Help
    document.getElementById('helpButton').addEventListener('click', helpPrompt);
};

// Functions for clearing forms, showing prompts, or hiding prompts

// Clear
function clearFields() {
    document.getElementById('addBillForm').reset();
}

// Search
function clearSearchField() {
    document.getElementById('searchBillForm').reset();
}

function showSearchPrompt() {
    document.getElementById('searchPrompt').style.display = 'block';
}

function cancelSearchPrompt() {
    document.getElementById('searchPrompt').style.display = 'none';
}

// Delete
function clearDeleteField() {
    document.getElementById('deleteBillForm').reset();
    
}

function cancelDeletePrompt() {
    document.getElementById('deletePrompt').style.display = 'none';
}

function showDeletePrompt() {
    document.getElementById('deletePrompt').style.display = 'block';
}

// Mark
function clearMarkField() {
    document.getElementById('markBillForm').reset();
}

function showMarkPrompt() {
    document.getElementById('markPrompt').style.display = 'block';
}

function cancelMarkPrompt() {
    document.getElementById('markPrompt').style.display = 'none';
}

// PDF
function clearPdfField() {
    document.getElementById('pdfBillForm').reset();
}

function showPdfPrompt() {
    document.getElementById('pdfPrompt').style.display = 'block';
}

function cancelPdfPrompt() {
    document.getElementById('pdfPrompt').style.display = 'none';
}

// Main Menu
function mainMenu(){
    window.location.href = 'main_menu.html';
}

// Help
function helpPrompt(){
    alert("To search bills click Search and in the pop up use Bill Id field to search. Appointment ID field can also be used for searching."+ 
    "\n\nEnsure all fields marked with * are filled out." + 
    "\n\nEnsure numerical value for all ID fields."+ 
    "\n\nTo create bill pdf click Generate PDF, enter bill id, and click create PDF. "+
    "\n\nTo mark bill as paid only use Bill ID and Is Paid fields. Type 'true' in Is Paid field to mark bill as paid." + 
    "\n\nType 'false' to mark as unpaid.",)

}

// Functions responsible for validation
function validateIsPaid(value) {
    const lowerCaseValue = value.toLowerCase();
    return lowerCaseValue === "true" || lowerCaseValue === "false";
}


// Function that fetches bill information for html table
function fetchBills(){
    // Fetch billing
    fetch('billing')
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
        const tbody = document.querySelector('#billingTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table
        data.forEach(bill => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${bill.billId}</td>
                    <td>${bill.appointmentId}</td>
                    <td>${bill.notes}</td>
                    <td>${bill.isPaid}</td>
                    <td>${bill.procedureOccurrences}</td>
                    <td>${bill.procedureId}</td>
                    <td>${bill.price}</td> 
                    <td>${bill.total}</td> 
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching bills:', error);
    });
}

// Function that searches bill in the database.
function searchBill(event) {
    event.preventDefault();

    const appointmentId = document.getElementById('appointmentId').value;
    const billId = document.getElementById('searchBillId').value;

     // Fetches method to search bills by appointmentID and billId.
    fetch(`searchBill?appointmentID=${appointmentId}&billID=${billId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const tbody = document.querySelector('#billingTable tbody');
            tbody.innerHTML = '';
            
            // After values above are submitted to java the table is updated to show bills based on search.
            data.forEach(bill => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${bill.billId}</td>
                    <td>${bill.appointmentId}</td>
                    <td>${bill.notes}</td>
                    <td>${bill.isPaid}</td>
                    <td>${bill.procedureOccurrences}</td>
                    <td>${bill.procedureId}</td>
                    <td>${bill.price}</td> 
                    <td>${bill.total}</td> 
                `;
                tbody.appendChild(row);
            });

        })
        .catch(error => {
            console.error('Error searching bills:', error);
            alert('Error searching bills. Please try again.');
        });
}

// Function that allows users to add new bills.
function addBill(event){
    event.preventDefault();

    // Get data from the addBillForm.
    const form = new FormData(document.getElementById('addBillForm'));

    // Validate field in addBillForm
    const isPaid = form.get('isPaid');
    if (isPaid && !validateIsPaid(isPaid)) {
        alert('Please enter a true or false for Is Paid');
        return;
    }

    // Fetch POST java method to take information entered in HTML and add bill to the database
    fetch('addBill', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams(form).toString()
    })

    .then(response => {
        if (!response.ok) {
            alert('Appointment Id: '+ form.get('appointmentId') + ' does not exist');
            throw new Error('Network response was not ok: ' + response.statusText);
            
        }
        // Updates table, sends message, and resets the addBillForm after bill is added.
        fetchBills();
        alert('Bill added successfully.');
        document.getElementById('addBillForm').reset();
    })
    .catch(error => {
        console.error('Error adding bill:', error);
        alert('Error adding bill. Please try again.');
    });
}

// Function that deletes bill from database.
function deleteBill(event){
    event.preventDefault();

    const billId = document.getElementById('deleteBillId').value;

    // Calls java DELETE method and sends billID value so java can remove bill.
    fetch(`deleteBill?billId=${billId}`, {
        method: 'DELETE',
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Provides message, updates table, resets form, and closes delete prompt after bill is deleted successfully.
        alert('Bill: ' + billId + ' deleted successfully or did not exist.');
        fetchBills();
        document.getElementById('deletePrompt').style.display = 'none';
        document.getElementById('deleteBillForm').reset();
    })
    .catch(error => {
        console.error('Error deleting bill:', error);
        alert('Error deleting bill. Please try again.');
    });

}

// Function that marks bills as paid or not paid
function markBill(event){
    event.preventDefault();

    // Get the billId for bill that will be updated.
    const billId = document.getElementById('markBillId').value;
    const form = new FormData(document.getElementById('addBillForm'));

    // Validation for field in addBillForm.
    const isPaid = form.get('isPaid');
    if (isPaid && !validateIsPaid(isPaid)) {
        alert('Please enter a true or false for Is Paid');
        return;
    }

    // Fetches the POST method to update is Paid for bill by bill Id.
    fetch(`markBill?billId=${billId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams(form).toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Refreshes the table, sends message, reset forms, and hide update popup.
        fetchBills(); 
        alert('Bill: ' + billId + ' updated successfully.');
        document.getElementById('markBillForm').reset();
        document.getElementById('markPrompt').style.display = 'none';
        document.getElementById('addBillForm').reset();
    })
    .catch(error => {
        console.error('Error marking bill:', error);
        alert('Error marking bill. Please try again.');
    });
}

// Function to create pdf for bill
function pdfBill(event){
    event.preventDefault();
        // Get the bill ID entered by the user
        const billId = document.getElementById('pdfBillId').value;
    
        // Send a POST request to the servlet to generate the PDF.
        fetch('pdfBill', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'billId=' + encodeURIComponent(billId)
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
            link.download = 'bill_' + billId + '.pdf';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        })
        .catch(error => {
            console.error('Error generating PDF:', error);
            alert('Error generating PDF. Bill ID: ' + billId + ' might not exist. Please try again.');
        });

}