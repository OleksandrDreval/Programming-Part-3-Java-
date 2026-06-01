async function loadBooks() {
    const response = await fetch('/api/books');
    const books = await response.json();
    const list = document.getElementById('bookList');
    list.innerHTML = '';
    books.forEach(book => {
        const item = document.createElement('li');
        item.textContent = `ID: ${book.id} | ${book.title} - ${book.author} (${book.publicationYear}) [${book.genre}]`;
        list.appendChild(item);
    });
}

async function createBook() {
    const book = {
        title: document.getElementById('title').value,
        author: document.getElementById('author').value,
        publicationYear: parseInt(document.getElementById('year').value),
        genre: document.getElementById('genre').value
    };
    const response = await fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(book)
    });
    if (response.ok) {
        alert('Book successfully added');
        loadBooks();
    } else {
        alert('Error adding book');
    }
}

async function loadOrders() {
    const response = await fetch('/api/orders');
    const orders = await response.json();
    const list = document.getElementById('orderList');
    list.innerHTML = '';
    orders.forEach(order => {
        const item = document.createElement('li');
        let bookTitles = order.books && order.books.length > 0 ? order.books.map(b => b.title).join(', ') : 'No books';
        let address = order.deliveryAddress ? `${order.deliveryAddress.city}, ${order.deliveryAddress.street}` : 'No address';
        item.textContent = `Order ${order.orderId} | ${order.customerName} | Delivery: ${address} | Status: ${order.status} | Books: ${bookTitles}`;
        list.appendChild(item);
    });
}

async function createOrder() {
    const bookId = parseInt(document.getElementById('bookId').value);

    const bookResponse = await fetch(`/api/books/${bookId}`);
    if (!bookResponse.ok) {
        alert('Book with this ID not found');
        return;
    }
    const book = await bookResponse.json();

    const order = {
        customerName: document.getElementById('customerName').value,
        books: [book],
        deliveryAddress: {
            city: document.getElementById('city').value,
            street: document.getElementById('street').value
        }
    };

    const response = await fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(order)
    });

    if (response.ok) {
        alert('Order successfully created');
        loadOrders();
    } else {
        const errorText = await response.text();
        alert('Error: ' + errorText);
    }
}
