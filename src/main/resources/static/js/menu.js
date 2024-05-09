function updateQuantity(menuItemId, quantity) {
    const inputElement = document.querySelector(`input[value="${menuItemId}"]`);
    const orderItemInput = inputElement.nextElementSibling;
    orderItemInput.value = quantity;
    console.log('quantity: ',quantity);
    console.log('menuItemId', menuItemId);
}

function decrementQuantity(event, menuItemId, price) {
    event.stopPropagation();
    event.preventDefault();

    if (products[menuItemId].quantity > 1) {
        products[menuItemId].quantity--;
    }

    document.getElementById(`quantity-${menuItemId}`).textContent = products[menuItemId].quantity;
    updatePrice(menuItemId);
    updateTotalPrice();

    updateQuantity(menuItemId, products[menuItemId].quantity);

    const itemElement = document.getElementById(`cart-item-${menuItemId}`);
    const quantityInput = itemElement.querySelector('input[name$="].quantity"]');
    quantityInput.value = products[menuItemId].quantity;

    updateWaitingTime(menuItemId); // 대기 시간 업데이트
}

function incrementQuantity(event, menuItemId, price) {
    event.stopPropagation();
    event.preventDefault();

    products[menuItemId].quantity++;

    document.getElementById(`quantity-${menuItemId}`).textContent = products[menuItemId].quantity;
    updatePrice(menuItemId);
    updateTotalPrice();

    updateQuantity(menuItemId, products[menuItemId].quantity);

    const itemElement = document.getElementById(`cart-item-${menuItemId}`);
    const quantityInput = itemElement.querySelector('input[name$="].quantity"]');
    quantityInput.value = products[menuItemId].quantity;

    updateWaitingTime(menuItemId); // 대기 시간 업데이트
}

function updatePrice(menuItemId) {
    const price = products[menuItemId].price * products[menuItemId].quantity;
    document.getElementById(`price-${menuItemId}`).textContent = `₩${price}`;
}

function updateTotalPrice() {
    let total = 0;
    for (const menuItemId in products) {
    total += products[menuItemId].price * products[menuItemId].quantity;
    }
    document.getElementById('total-price').textContent = `₩${total}`;
}

function fetchMenuItems(category) {
    fetch(`/menu/category/${category}`)
        .then(response => response.json())
        .then(data => {
            updateMenuItems(data);

            // 현재 열려 있는 팝오버 모두 숨기기
            const openPopovers = document.querySelectorAll('.popover.show');
            openPopovers.forEach(popover => {
                popover.remove();
            });

            //팝오버 초기화
            const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
            [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function updateWaitingTime(menuItemId) {
    const timeElement = document.querySelector(`[data-menu-id="${menuItemId}"]`);
  
    if (timeElement) {
      const eventQuantity = parseInt(timeElement.dataset.eventQuantity);
      const eventTime = parseInt(timeElement.dataset.eventTime);
      const defaultTime = parseInt(timeElement.dataset.defaultTime);
  
      const cartItemQuantity = getCartItemQuantity(menuItemId);
      const remainingQuantity = eventQuantity - cartItemQuantity;
  
      const resultTime = remainingQuantity >= 0 ? eventTime : defaultTime;
      timeElement.textContent = `${resultTime} mins`;
    }
  }
  
  function getCartItemQuantity(menuItemId) {
    var cartItems = document.querySelectorAll('#cartItems li');
    for (var i = 0; i < cartItems.length; i++) {
      var cartItem = cartItems[i];
      var cartItemId = cartItem.querySelector('input[name$=".menuItemId"]').value;
      if (cartItemId === menuItemId) {
        var quantityInput = cartItem.querySelector('input[name$=".quantity"]');
        return parseInt(quantityInput.value);
      }
    }
    return 0;
  }

function updateMenuItems(menuItems) {
    const menuContainer = document.querySelector('.row.row-cols-1.row-cols-sm-2.row-cols-md-3.g-3');
    menuContainer.innerHTML = '';

    menuItems.forEach(menuItem => {
    const menuElement = createMenuElement(menuItem);
    menuContainer.appendChild(menuElement);
    });
}

function createMenuElement(menuItem) {
    const colElement = document.createElement('div');
    colElement.classList.add('col');

    const cardElement = document.createElement('div');
    cardElement.classList.add('card', 'shadow-sm');

    const imageElement = document.createElement('img');
    imageElement.src = menuItem.imagePath;
    imageElement.classList.add('bd-placeholder-img', 'card-img-top');
    imageElement.alt = 'Thumbnail';
    imageElement.style.width = '100%';
    imageElement.style.height = '225px';
    imageElement.style.objectFit = 'cover';

    const cardBodyElement = document.createElement('div');
    cardBodyElement.classList.add('card-body');

    const menuNameElement = document.createElement('p');
    menuNameElement.classList.add('card-text');
    menuNameElement.textContent = menuItem.menuName;

    const priceElement = document.createElement('p');
    priceElement.classList.add('card-text');
    priceElement.textContent = `₩${menuItem.price}`;

    const buttonGroupElement = document.createElement('div');
    buttonGroupElement.classList.add('btn-group');

    const viewButtonElement = document.createElement('button');
    viewButtonElement.type = 'button';
    viewButtonElement.classList.add('btn', 'btn-sm', 'btn-outline-secondary');
    viewButtonElement.dataset.bsContainer = 'body';
    viewButtonElement.dataset.bsToggle = 'popover';
    viewButtonElement.dataset.bsPlacement = 'bottom';
    viewButtonElement.dataset.bsContent = `${menuItem.description}`;
    viewButtonElement.textContent = 'View';

    const addButtonElement = document.createElement('button');
    addButtonElement.type = 'button';
    addButtonElement.classList.add('btn', 'btn-sm', 'btn-outline-secondary');
    addButtonElement.setAttribute('onclick', `addToCart({ id: '${menuItem.id}', menuName: '${menuItem.menuName}', price: '${menuItem.price}' })`);
    addButtonElement.textContent = 'Add';

    const timeElement = document.createElement('small');
    timeElement.classList.add('text-muted');
    timeElement.dataset.menuId = menuItem.id; // data-menu-id 속성 추가
    timeElement.dataset.eventQuantity = menuItem.eventQuantity; // data-event-quantity 속성 추가
    timeElement.dataset.eventTime = menuItem.eventTime; // data-event-time 속성 추가
    timeElement.dataset.defaultTime = menuItem.defaultTime; // data-default-time 속성 추가
    timeElement.textContent = menuItem.eventTime > 0 ? `${menuItem.eventTime} mins` : `${menuItem.defaultTime} mins`;

    buttonGroupElement.appendChild(viewButtonElement);
    buttonGroupElement.appendChild(addButtonElement);

    const buttonContainerElement = document.createElement('div');
    buttonContainerElement.classList.add('d-flex', 'justify-content-between', 'align-items-center');
    buttonContainerElement.appendChild(buttonGroupElement);
    buttonContainerElement.appendChild(timeElement);

    cardBodyElement.appendChild(menuNameElement);
    cardBodyElement.appendChild(priceElement);
    cardBodyElement.appendChild(buttonContainerElement);

    cardElement.appendChild(imageElement);
    cardElement.appendChild(cardBodyElement);

    colElement.appendChild(cardElement);

    return colElement;
}

function addToCart(menuItem) {
    const existingItem = document.getElementById(`cart-item-${menuItem.id}`);

    if (existingItem) {
        products[menuItem.id].quantity++;
        document.getElementById(`quantity-${menuItem.id}`).textContent = products[menuItem.id].quantity;
        updatePrice(menuItem.id);
        updateQuantity(menuItem.id, products[menuItem.id].quantity);
    } else {
        const cartItemsContainer = document.getElementById('cartItems');
        const itemElement = createCartItemElement(menuItem);
        cartItemsContainer.insertBefore(itemElement, cartItemsContainer.lastChild);
        products[menuItem.id] = {
        quantity: 1,
        price: menuItem.price
        };
    }

    updateCartInStorage(menuItem);
    showAddedConfirmation(menuItem);
    updateTotalPrice();
    updateWaitingTime(menuItem.id); // 대기 시간 업데이트
}

function updateCartInStorage(menuItem) {
    let cartItems = JSON.parse(sessionStorage.getItem('cartItems') || '[]');
    const existingItemIndex = cartItems.findIndex(item => item.id === menuItem.id);

    if (existingItemIndex !== -1) {
        cartItems[existingItemIndex].quantity = products[menuItem.id].quantity;
    } else {
        cartItems.push({ ...menuItem, quantity: 1 });
    }

    sessionStorage.setItem('cartItems', JSON.stringify(cartItems));
}

function showAddedConfirmation(menuItem) {
    const addButton = document.querySelector(`button[onclick="addToCart({ id: '${menuItem.id}', menuName: '${menuItem.menuName}', price: '${menuItem.price}' })"]`);
    const popover = new bootstrap.Popover(addButton, {
    content: '메뉴가 추가되었습니다.',
    trigger: 'manual',
    placement: 'bottom'
    });

    popover.show();
    setTimeout(() => popover.hide(), 1000);
}

function removeToCart(event, menuItemId) {
    event.stopPropagation();
    event.preventDefault();

    const itemElement = document.getElementById(`cart-item-${menuItemId}`);
    itemElement.remove();

    let cartItems = JSON.parse(sessionStorage.getItem('cartItems') || '[]');
    cartItems = cartItems.filter(item => item.id !== menuItemId);
    sessionStorage.setItem('cartItems', JSON.stringify(cartItems));

    delete products[menuItemId];

    updateTotalPrice();
}

function createCartItemElement(menuItem) {
    const itemElement = document.createElement('li');
    itemElement.id = `cart-item-${menuItem.id}`;
    itemElement.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');

    const nameElement = document.createElement('div');
    nameElement.textContent = menuItem.menuName;
    nameElement.classList.add('me-auto'); // 메뉴 이름 길이에 상관없이 왼쪽에 고정

    const quantityElement = document.createElement('div');
    quantityElement.classList.add('d-flex', 'align-items-center'); // 수량 부분 가운데 정렬

    const decrementButton = document.createElement('button');
    decrementButton.classList.add('btn', 'btn-sm', 'btn-outline-secondary');
    decrementButton.textContent = '-';
    decrementButton.addEventListener('click', (event) => decrementQuantity(event, menuItem.id, menuItem.price));

    const quantitySpan = document.createElement('span');
    quantitySpan.id = `quantity-${menuItem.id}`;
    quantitySpan.classList.add('mx-2'); // 수량 양쪽 여백 조절
    quantitySpan.textContent = '1';

    const incrementButton = document.createElement('button');
    incrementButton.classList.add('btn', 'btn-sm', 'btn-outline-secondary');
    incrementButton.textContent = '+';
    incrementButton.addEventListener('click', (event) => incrementQuantity(event, menuItem.id, menuItem.price));

    quantityElement.appendChild(decrementButton);
    quantityElement.appendChild(quantitySpan);
    quantityElement.appendChild(incrementButton);

    const priceElement = document.createElement('span');
    priceElement.id = `price-${menuItem.id}`;
    priceElement.classList.add('text-body-secondary', 'ms-2'); // 가격 왼쪽 여백 추가
    priceElement.textContent = `₩${menuItem.price}`;

    const removeButton = document.createElement('button');
    removeButton.classList.add('btn', 'btn-sm', 'btn-outline-secondary', 'ms-2'); // 제거 버튼 왼쪽 여백 추가
    removeButton.textContent = 'x';
    removeButton.addEventListener('click', (event) => removeToCart(event, menuItem.id));

    const menuItemIdInput = document.createElement('input');
    menuItemIdInput.type = 'hidden';
    menuItemIdInput.name = 'orderItems[' + (document.querySelectorAll('#cartItems li').length) + '].menuItemId';
    menuItemIdInput.value = menuItem.id;

    const quantityInput = document.createElement('input');
    quantityInput.type = 'hidden';
    quantityInput.name = 'orderItems[' + (document.querySelectorAll('#cartItems li').length) + '].quantity';
    quantityInput.value = 1;

    itemElement.appendChild(nameElement);
    itemElement.appendChild(quantityElement);
    itemElement.appendChild(priceElement);
    itemElement.appendChild(removeButton);
    itemElement.appendChild(menuItemIdInput);
    itemElement.appendChild(quantityInput);

    return itemElement;
}