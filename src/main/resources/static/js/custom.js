$(document).ready(
    function () {
        var radioButtons = document.getElementsByClassName("radio-buttons");
        for (var i = 0; i < radioButtons.length; i++) {
            radioButtons[i].addEventListener("click", function (event) {
                    var radioButtonName = event.target.innerHTML;
                    var columnDiv = document.getElementById('contact-input-parent-column');
                    if (radioButtonName === 'Username') {
                        // document.getElementById('search-form-country-code').style.display = "none";
                        columnDiv.classList.remove('col-md-8');
                        columnDiv.classList.add('col-md-12');
                        document.getElementById('contact').placeholder = "Username";
                        document.getElementsByClassName('contactInfo')[0].innerText = "Enter username";
                        if (!document.getElementsByClassName('mdi-account')[0]) {
                            document.getElementsByClassName('icons')[0].classList.remove('mdi-phone');
                            document.getElementsByClassName('icons')[0].classList.remove('mdi-email');
                            document.getElementsByClassName('icons')[0].classList.add('mdi-account');
                        }
                    } else if (radioButtonName === 'Email') {
                        // document.getElementById('search-form-country-code').style.display = "none";
                        columnDiv.classList.remove('col-md-8');
                        columnDiv.classList.add('col-md-12');
                        document.getElementById('contact').placeholder = "Email";
                        document.getElementsByClassName('contactInfo')[0].innerText = "Enter email";
                        if (!document.getElementsByClassName('mdi-email')[0]) {
                            document.getElementsByClassName('icons')[0].classList.remove('mdi-phone');
                            document.getElementsByClassName('icons')[0].classList.remove('mdi-account');
                            document.getElementsByClassName('icons')[0].classList.add('mdi-email');
                        }
                    } else if (radioButtonName === 'Phone') {
                        // document.getElementById('search-form-country-code').style.display = "inline-block";
                        columnDiv.classList.remove('col-md-12');
                        columnDiv.classList.add('col-md-8');
                        document.getElementById('contact').placeholder = "Phone";
                        document.getElementsByClassName('contactInfo')[0].innerText = "Enter phone";
                        if (!document.getElementsByClassName('mdi-phone')[0]) {
                            document.getElementsByClassName('icons')[0].classList.remove('mdi-email');
                            document.getElementsByClassName('icons')[0].classList.remove('mdi-account');
                            document.getElementsByClassName('icons')[0].classList.add('mdi-phone');
                        }
                    }
                }
            )
        }
    }
);


$(document).ready(function () {
    $('input[name="recipient"]').on('click', function () {
        $('#transaction-data').css('display', 'inline-block');
    });

    $('#external-transaction-form').submit(function () {
        let recipientId = $('input[name="recipient"]:checked').val();
        $('#recipient-id').val(recipientId);
    })
});


$(document).ready(function () {
    // Set initial values of transaction direction checkboxes.
    let direction = $('input[name="direction"]').val();
    if (direction === 'INCOMING' || direction === 'ALL') {
        $('#direction-incoming').prop('checked', true);
    }
    if (direction === 'OUTGOING' || direction === 'ALL') {
        $('#direction-outgoing').prop('checked', true);
    }

    // Set initial values of sort direction options
    let sortByAmount = $('#sort-by-amount');
    let amountDirection = $('#amount-asc-or-desc').text();
    if (amountDirection !== 'none') {
        $(sortByAmount).prop('checked', true);
        $('input[name="amount"]').prop('disabled', false);
        if (amountDirection === 'asc') {
            $('#sort-by-amount-ascending').prop('checked', true);
        } else {
            $('#sort-by-amount-descending').prop('checked', true);
        }
    }

    let sortByDate = $('#sort-by-date');
    let dateDirection = $('#date-asc-or-desc').text();
    if (dateDirection !== 'none') {
        $(sortByDate).prop('checked', true);
        $('input[name="date"]').prop('disabled', false);
        if (dateDirection === 'asc') {
            $('#sort-by-date-ascending').prop('checked', true);
        } else {
            $('#sort-by-date-descending').prop('checked', true);
        }
    }

    // Hide ASC/DESC radio buttons if sort attribute is deselected
    $(sortByAmount).change(function () {
        let amount = $('input[name="amount"]');
        if ($(this).is(':checked')) {
            $(amount).prop('disabled', false);
        } else {
            $(amount).prop('disabled', true);
        }
    });

    $(sortByDate).change(function () {
        let date = $('input[name="date"]');
        if ($(this).is(':checked')) {
            $(date).prop('disabled', false);
        } else {
            $(date).prop('disabled', true);
        }
    });

    // Add query parameters
    $('#query-form').submit(function () {
        // Transfer direction
        let incoming = $('#direction-incoming');
        let outgoing = $('#direction-outgoing');
        let direction = $('input[name="direction"]');
        if ($(incoming).is(':checked') && $(outgoing).is(':checked')) {
            $(direction).val('ALL');
        } else if ($(incoming).is(':checked')) {
            $(direction).val('INCOMING');
        } else if ($(outgoing).is(':checked')) {
            $(direction).val('OUTGOING');
        } else {
            $(direction).val('NONE');
        }
    });


    $('#upload-image').on("change", function () {
        let fileName = $(this).val();
        $('#file-selected').html(fileName);

        if (this.files && this.files[0]) {
            let submitButton = $('#submit');
            let sizeError = $('#size-error');
            if (this.files[0].size > 3 * Math.pow(2, 20)) {
                $(submitButton).css({display: "none"});
                $(sizeError).css({display: "inline-block"});
            } else {
                $(submitButton).css({display: "inline-block"});
                $(sizeError).css({display: "none"});
            }
        }
    });

    $('#top-up-with-donation').on('click', function (event) {
        event.preventDefault();
        $('#with-donation-value').val('true');
        $('#top-up-form').submit();
    });
});

$(document).ready(function () {
        var amount = document.getElementsByClassName('currency-format');
        var formatter = new Intl.NumberFormat('bg-BG', {
            style: 'currency',
            currency: 'USD'
        });
        // for (var i = 0; 0 <= amount.length; i++) {
        //     // amount[i].innerHTML = (Math.round(amount[i].innerHTML * 100) / 100).toFixed(2);
        //     amount[i].innerHTML = formatter.format(amount[i].innerHTML);
        // }
    }
);

$(document).ready(function () {
        var amount = document.getElementsByClassName('transactions-amount-format');
        var formatter = new Intl.NumberFormat('bg-BG', {
            style: 'currency',
            signDisplay: 'always',
            currency: 'USD'
        });
        // for (var i = 0; 0 <= amount.length; i++) {
        //     // amount[i].innerHTML = (Math.round(amount[i].innerHTML * 100) / 100).toFixed(2);
        //     amount[i].innerHTML = formatter.format(amount[i].innerHTML);
        // }
    }
);

$(document).ready(function () {
        var amount = document.getElementsByClassName('admin-transactions-amount-format');
        var formatter = new Intl.NumberFormat('bg-BG', {
            style: 'currency',
            currency: 'USD'
        });
        // for (var i = 0; 0 <= amount.length; i++) {
        //     // amount[i].innerHTML = (Math.round(amount[i].innerHTML * 100) / 100).toFixed(2);
        //     amount[i].innerHTML = formatter.format(amount[i].innerHTML);
        // }
    }
);

$(document).ready(function () {
    var body = $('body');
    body.on('change', '#wallet', function () {
        var selectedWalletId = $(this).val(); // Get the selected wallet ID
        $('input[name="senderWalletId"]').val(selectedWalletId); // Set the selected wallet ID as the value of senderWalletId input field
    });
    body.on('change', '#amount', function () {
        $('#amount-confirm').val(this.value);
    });
    body.on('change', '#description', function () {
        $('#description-confirm').val(this.value);
    });
    var select = document.getElementById("wallet");
    var chosenWallet = select.options[select.selectedIndex].text;
    $('#wallet-confirm').val(chosenWallet);
    body.on('change', '#wallet', function () {
        var newChosenWallet = select.options[select.selectedIndex].text;
        $('#wallet-confirm').val(newChosenWallet);
    });
    body.on('change', 'input[name="recipient"]', function () {
        var chosenRecipientId = "recipient" + $('input[name="recipient"]:checked').val();
        var chosenRecipientUsername = $('label[for="' + chosenRecipientId + '"]').text();
        $('#recipient-confirm').val(chosenRecipientUsername);
    });
});

$(document).ready(function () {
    var body = $('body');

    body.on('change', '#wallet', function () {
        var receiverWalletId = $(this).val(); // Get the selected wallet ID
        $('input[name="receiverWalletId"]').val(receiverWalletId); // Set the selected wallet ID as the value of senderWalletId input field
    });

    body.on('change', '#card', function () {
        var cardId = $(this).val(); // Get the selected card ID
        $('input[name="cardId"]').val(cardId); // Set the selected card ID as the value of the cardId input field
    });

    body.on('change', '#amount', function () {
        $('#amount-confirm').val(this.value);
    });

    body.on('change', '#description', function () {
        $('#description-confirm').val(this.value);
    });

    var select = document.getElementById("wallet");
    var chosenWallet = select.options[select.selectedIndex].text;
    $('#wallet-confirm').val(chosenWallet);
    body.on('change', '#wallet', function () {
        var newChosenWallet = select.options[select.selectedIndex].text;
        $('#wallet-confirm').val(newChosenWallet);
    });

    var selectCard = document.getElementById("card");
    var chosenCard = selectCard.options[selectCard.selectedIndex].text;
    $('#card-confirm').val(chosenCard);
    body.on('change', '#card', function () {
        var newChosenCard = selectCard.options[selectCard.selectedIndex].text;
        $('#card-confirm').val(newChosenCard);
    });
});
$(document).ready(function () {
    $('#start-date').datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true
    });

    $('#end-date').datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true
    });
});

$(document).ready(function () {
    $('#transactionType').change(function () {
        var selectedType = $(this).val();
        if (selectedType === 'RECURRING') {
            $('#additionalFields').show();
        } else {
            $('#additionalFields').hide();
        }
    });
});