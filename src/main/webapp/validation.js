/**
 * Validation Module for Bestellsystem
 * Provides real-time input validation with user-friendly error messages
 */

const Validation = {
    // Validation patterns
    patterns: {
        email: /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/,
        phone: /^[\d\s\-\+\(\)]+$/,
        postcode: /^\d{5}$/,
        date: /^\d{4}-\d{2}-\d{2}$/,
        name: /^[a-zA-ZäöüÄÖÜß\s\-']+$/,
        street: /^[a-zA-ZäöüÄÖÜß\s\-\.0-9]+$/,
        number: /^\d+[a-zA-Z]?$/,
        price: /^\d+(\.\d{1,2})?$/,
        integer: /^\d+$/
    },

    // Error messages
    messages: {
        required: 'Dieses Feld ist erforderlich',
        email: 'Bitte gültige E-Mail-Adresse eingeben',
        phone: 'Bitte gültige Telefonnummer eingeben',
        postcode: 'Postleitzahl muss 5 Zahlen enthalten',
        date: 'Datum muss im Format yyyy-mm-dd sein',
        dateInvalid: 'Bitte gültiges Datum eingeben',
        dateFuture: 'Datum darf nicht in der Zukunft liegen',
        dateAge: 'Person muss mindestens 14 Jahre alt sein',
        name: 'Bitte keine Sonderzeichen verwenden',
        street: 'Bitte gültigen Straßennamen eingeben',
        number: 'Bitte gültige Hausnummer eingeben',
        price: 'Bitte gültigen Preis eingeben',
        priceNegative: 'Der Preis muss positiv sein',
        integer: 'Bitte ganze Zahl eingeben',
        integerNegative: 'Der Wert muss positiv sein',
        stockNegative: 'Lagerbestand kann nicht negativ sein',
        minLength: 'Mindestens {min} Zeichen erforderlich',
        maxLength: 'Maximal {max} Zeichen erlaubt'
    },

    // Show error message next to label
    showError(input, message) {
        this.clearError(input);
        input.classList.add('border-red-500', 'focus:ring-red-500');
        input.classList.remove('border-gray-300', 'focus:ring-blue-500', 'focus:ring-green-500', 'focus:ring-purple-500');
        
        // Find the label element
        const label = input.parentElement.querySelector('label');
        if (label) {
            // Create error span and insert it after the label text
            const errorSpan = document.createElement('span');
            errorSpan.className = 'validation-error text-red-500 text-xs ml-2 font-normal';
            errorSpan.textContent = `${message}`;
            label.appendChild(errorSpan);
        }
    },

    // Clear error message
    clearError(input) {
        input.classList.remove('border-red-500', 'focus:ring-red-500');
        input.classList.add('border-gray-300');
        
        // Remove error span from label
        const label = input.parentElement.querySelector('label');
        if (label) {
            const errorSpan = label.querySelector('.validation-error');
            if (errorSpan) {
                errorSpan.remove();
            }
        }
    },

    // Show success state
    showSuccess(input) {
        this.clearError(input);
        input.classList.remove('border-red-500');
        input.classList.add('border-green-500');
        setTimeout(() => {
            input.classList.remove('border-green-500');
            input.classList.add('border-gray-300');
        }, 2000);
    },

    // Validate email
    validateEmail(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && !this.patterns.email.test(value)) {
            this.showError(input, this.messages.email);
            return false;
        }
        
        if (value) {
            this.showSuccess(input);
        }
        return true;
    },

    // Validate phone number
    validatePhone(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && !this.patterns.phone.test(value)) {
            this.showError(input, this.messages.phone);
            return false;
        }
        
        if (value && value.length < 6) {
            this.showError(input, this.messages.phone);
            return false;
        }
        
        if (value) {
            this.showSuccess(input);
        }
        return true;
    },

    // Validate postcode
    validatePostcode(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && !this.patterns.postcode.test(value)) {
            this.showError(input, this.messages.postcode);
            return false;
        }
        
        if (value) {
            this.showSuccess(input);
        }
        return true;
    },

    // Validate date
    validateDate(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && !this.patterns.date.test(value)) {
            this.showError(input, this.messages.date);
            return false;
        }
        
        if (value) {
            const date = new Date(value);
            if (isNaN(date.getTime())) {
                this.showError(input, this.messages.dateInvalid);
                return false;
            }
            
            // Check if date is not in the future
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            if (date > today) {
                this.showError(input, this.messages.dateFuture);
                return false;
            }
            
            // Check if person is at least 18 years old (for birthday field)
            if (input.id === 'kunde-geburtstag') {
                const age = this.calculateAge(date);
                if (age < 14) {
                    this.showError(input, this.messages.dateAge);
                    return false;
                }
            }
            
            this.showSuccess(input);
        }
        return true;
    },

    // Calculate age from date
    calculateAge(birthDate) {
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    },

    // Validate name (first name, last name, city, country)
    validateName(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && value.length < 2) {
            this.showError(input, this.messages.minLength.replace('{min}', '2'));
            return false;
        }
        
        if (value && !this.patterns.name.test(value)) {
            this.showError(input, this.messages.name);
            return false;
        }
        
        if (value) {
            this.showSuccess(input);
        }
        return true;
    },

    // Validate street name
    validateStreet(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && value.length < 2) {
            this.showError(input, this.messages.minLength.replace('{min}', '2'));
            return false;
        }
        
        if (value && !this.patterns.street.test(value)) {
            this.showError(input, this.messages.street);
            return false;
        }
        
        if (value) {
            this.showSuccess(input);
        }
        return true;
    },

    // Validate house number
    validateHouseNumber(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && !this.patterns.number.test(value)) {
            this.showError(input, this.messages.number);
            return false;
        }
        
        if (value) {
            this.showSuccess(input);
        }
        return true;
    },

    // Validate price
    validatePrice(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && !this.patterns.price.test(value)) {
            this.showError(input, this.messages.price);
            return false;
        }
        
        if (value) {
            const price = parseFloat(value);
            if (price < 0) {
                this.showError(input, this.messages.priceNegative);
                return false;
            }
            if (price === 0) {
                this.showError(input, this.messages.priceNegative);
                return false;
            }
            this.showSuccess(input);
        }
        return true;
    },

    // Validate integer (for stock, quantity, etc.)
    validateInteger(input) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && !this.patterns.integer.test(value)) {
            this.showError(input, this.messages.integer);
            return false;
        }
        
        if (value) {
            const num = parseInt(value);
            if (num < 0) {
                this.showError(input, this.messages.integerNegative);
                return false;
            }
            
            // Special validation for stock
            if (input.id === 'produkt-lagerbestand' && num < 0) {
                this.showError(input, this.messages.stockNegative);
                return false;
            }
            
            this.showSuccess(input);
        }
        return true;
    },

    // Validate text with min/max length
    validateText(input, minLength = 0, maxLength = 500) {
        const value = input.value.trim();
        
        if (input.required && !value) {
            this.showError(input, this.messages.required);
            return false;
        }
        
        if (value && minLength > 0 && value.length < minLength) {
            this.showError(input, this.messages.minLength.replace('{min}', minLength));
            return false;
        }
        
        if (value && value.length > maxLength) {
            this.showError(input, this.messages.maxLength.replace('{max}', maxLength));
            return false;
        }
        
        if (value) {
            this.showSuccess(input);
        }
        return true;
    },

    // Validate select field
    validateSelect(select) {
        const value = select.value;
        
        if (select.required && !value) {
            this.showError(select, this.messages.required);
            return false;
        }
        
        if (value) {
            this.showSuccess(select);
        }
        return true;
    },

    // Initialize validation for all inputs
    init() {
        // Kunde fields
        this.attachValidator('kunde-vorname', () => this.validateName(document.getElementById('kunde-vorname')));
        this.attachValidator('kunde-name', () => this.validateName(document.getElementById('kunde-name')));
        this.attachValidator('kunde-email', () => this.validateEmail(document.getElementById('kunde-email')));
        this.attachValidator('kunde-geburtstag', () => this.validateDate(document.getElementById('kunde-geburtstag')));
        this.attachValidator('kunde-strasse', () => this.validateStreet(document.getElementById('kunde-strasse')));
        this.attachValidator('kunde-hausnummer', () => this.validateHouseNumber(document.getElementById('kunde-hausnummer')));
        this.attachValidator('kunde-postleitzahl', () => this.validatePostcode(document.getElementById('kunde-postleitzahl')));
        this.attachValidator('kunde-ort', () => this.validateName(document.getElementById('kunde-ort')));
        this.attachValidator('kunde-land', () => this.validateName(document.getElementById('kunde-land')));
        this.attachValidator('kunde-telefonnummer', () => this.validatePhone(document.getElementById('kunde-telefonnummer')));
        this.attachValidator('kunde-mobilnummer', () => this.validatePhone(document.getElementById('kunde-mobilnummer')));
        this.attachValidator('kunde-geschlecht', () => this.validateSelect(document.getElementById('kunde-geschlecht')));

        // Produkt fields
        this.attachValidator('produkt-name', () => this.validateText(document.getElementById('produkt-name'), 2, 200));
        this.attachValidator('produkt-preis', () => this.validatePrice(document.getElementById('produkt-preis')));
        this.attachValidator('produkt-lagerbestand', () => this.validateInteger(document.getElementById('produkt-lagerbestand')));
        this.attachValidator('produkt-beschreibung', () => this.validateText(document.getElementById('produkt-beschreibung'), 0, 500));

        // Bestellung fields
        this.attachValidator('bestellung-kunde', () => this.validateSelect(document.getElementById('bestellung-kunde')));

        // Rechnung fields
        this.attachValidator('create-rechnung-bestellung-id', () => this.validateInteger(document.getElementById('create-rechnung-bestellung-id')));

        console.log('Validation module initialized');
    },

    // Attach validator to input field
    attachValidator(fieldId, validatorFn) {
        const field = document.getElementById(fieldId);
        if (!field) return;

        // Validate on blur
        field.addEventListener('blur', validatorFn);

        // Clear error on focus
        field.addEventListener('focus', () => {
            this.clearError(field);
        });

        // Validate on input (with debounce for better UX)
        let timeout;
        field.addEventListener('input', () => {
            clearTimeout(timeout);
            timeout = setTimeout(validatorFn, 500);
        });
    },

    // Validate dynamic position fields in Bestellung form
    validatePositionFields() {
        const container = document.getElementById('bestellung-positionen');
        if (!container) return true;

        const positions = container.querySelectorAll('div');
        let allValid = true;

        positions.forEach(pos => {
            const produktSelect = pos.querySelector('.position-produkt');
            const mengeInput = pos.querySelector('.position-menge');

            if (produktSelect && !produktSelect.value) {
                this.showError(produktSelect, this.messages.required);
                allValid = false;
            } else if (produktSelect) {
                this.clearError(produktSelect);
            }

            if (mengeInput) {
                const menge = parseInt(mengeInput.value);
                if (!mengeInput.value || isNaN(menge) || menge < 1) {
                    this.showError(mengeInput, 'Menge muss mindestens 1 sein');
                    allValid = false;
                } else {
                    this.clearError(mengeInput);
                }
            }
        });

        return allValid;
    },

    // Validate entire form
    validateForm(formId) {
        const form = document.getElementById(formId);
        if (!form) return true;

        const inputs = form.querySelectorAll('input[required], select[required]');
        let allValid = true;

        inputs.forEach(input => {
            const fieldId = input.id;
            let isValid = true;

            // Determine validation type based on field ID
            if (fieldId.includes('email')) {
                isValid = this.validateEmail(input);
            } else if (fieldId.includes('telefon') || fieldId.includes('mobil')) {
                isValid = this.validatePhone(input);
            } else if (fieldId.includes('postleitzahl')) {
                isValid = this.validatePostcode(input);
            } else if (fieldId.includes('geburtstag')) {
                isValid = this.validateDate(input);
            } else if (fieldId.includes('preis')) {
                isValid = this.validatePrice(input);
            } else if (fieldId.includes('lagerbestand') || fieldId.includes('menge')) {
                isValid = this.validateInteger(input);
            } else if (fieldId.includes('strasse')) {
                isValid = this.validateStreet(input);
            } else if (fieldId.includes('hausnummer')) {
                isValid = this.validateHouseNumber(input);
            } else if (input.tagName === 'SELECT') {
                isValid = this.validateSelect(input);
            } else if (input.type === 'text') {
                if (fieldId.includes('vorname') || fieldId.includes('name') || fieldId.includes('ort') || fieldId.includes('land')) {
                    isValid = this.validateName(input);
                } else {
                    isValid = this.validateText(input);
                }
            } else if (input.type === 'number') {
                if (fieldId.includes('preis')) {
                    isValid = this.validatePrice(input);
                } else {
                    isValid = this.validateInteger(input);
                }
            }

            if (!isValid) {
                allValid = false;
            }
        });

        // Special handling for bestellung positions
        if (formId === 'bestellung-form') {
            if (!this.validatePositionFields()) {
                allValid = false;
            }
        }

        return allValid;
    }
};

// Initialize validation when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => Validation.init());
} else {
    Validation.init();
}

// Export for use in other scripts
window.Validation = Validation;
