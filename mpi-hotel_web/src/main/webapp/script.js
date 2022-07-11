function delete_selected_booking() {
    let radio_buttons = document.getElementsByClassName("booking-item");

    for (let i = 0; i < radio_buttons.length; i++) {
        if (radio_buttons[i].firstChild.checked) {
            let url = '/mpi-hotel_web/deleteBooking?bookingIdToDelete=' + radio_buttons[i].id;

            fetch(url, {
                method: 'post'
            }).then((response) => {
                radio_buttons[i].remove();
            });

            break;
        }
    }
}