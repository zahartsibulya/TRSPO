import threading
PI = 3.14159

def lenght_circle(r):
    c = 2*PI*r
    print(f"Довжина кола = {c}")

def area_circle(r):
    s = PI*r*r
    print(f"Площа кола = {s}")

t1 = threading.Thread(target = lenght_circle)
t2 = threading.Thread(target = area_circle)

t1.start()
t2.start()

t1.join()
t2.join()

if __name__ == "__main__":
    r = float(input("Введіть радіус: "))
    lenght_circle(r)
    area_circle(r)