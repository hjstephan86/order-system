import xml.etree.ElementTree as ET
from collections import defaultdict

# Parse the PMD XML file
tree = ET.parse('target/pmd.xml')
root = tree.getroot()

# Define namespace
ns = {'pmd': 'http://pmd.sourceforge.net/report/2.0.0'}

# Data structures to store complexity info
classes = {}
methods = defaultdict(list)

# Extract complexity information
for file_elem in root.findall('pmd:file', ns):
    for violation in file_elem.findall('pmd:violation', ns):
        rule = violation.get('rule')
        
        if rule == 'CyclomaticComplexity':
            class_name = violation.get('class')
            method_name = violation.get('method')
            text = violation.text.strip()
            
            if method_name:
                # Extract method complexity
                if 'has a cyclomatic complexity of' in text:
                    complexity = int(text.split('has a cyclomatic complexity of')[1].strip().rstrip('.'))
                    methods[class_name].append({
                        'name': method_name,
                        'complexity': complexity,
                        'line': violation.get('beginline')
                    })
            else:
                # Extract class complexity
                if 'has a total cyclomatic complexity of' in text:
                    parts = text.split('has a total cyclomatic complexity of')[1].strip()
                    total_complexity = int(parts.split('(')[0].strip())
                    highest_complexity = int(parts.split('highest')[1].strip().rstrip(').'))
                    classes[class_name] = {
                        'total': total_complexity,
                        'highest': highest_complexity,
                        'package': violation.get('package')
                    }

# Sort classes by total complexity (descending)
sorted_classes = sorted(classes.items(), key=lambda x: x[1]['total'], reverse=True)

# Write output to file
with open('cc.txt', 'w', encoding='utf-8') as f:
    f.write("=" * 80 + "\n")
    f.write("CYCLOMATIC COMPLEXITY REPORT\n")
    f.write("=" * 80 + "\n\n")
    
    for class_name, class_info in sorted_classes:
        f.write(f"\nClass: {class_name}\n")
        f.write(f"Package: {class_info['package']}\n")
        f.write(f"Total Complexity: {class_info['total']}\n")
        f.write(f"Highest Method Complexity: {class_info['highest']}\n")
        f.write("-" * 80 + "\n")
        
        # Sort methods by complexity (descending)
        if class_name in methods:
            sorted_methods = sorted(methods[class_name], key=lambda x: x['complexity'], reverse=True)
            f.write("Methods:\n")
            for method in sorted_methods:
                f.write(f"  - {method['name']}: {method['complexity']} (line {method['line']})\n")
        else:
            f.write("No methods found\n")
        
        f.write("\n")
    
    f.write("=" * 80 + "\n")
    f.write("END OF REPORT\n")
    f.write("=" * 80 + "\n")

print("Complexity report generated: cc.txt")
