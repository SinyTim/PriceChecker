from app import  db
from app import Product
from app import Store
from app import User


if __name__ == '__main__':

    db.create_all()

    store0 = Store(address='г. Минск 0000')
    store1 = Store(address='г. Минск 0001')

    user0 = User(name='Тимур')
    user1 = User(name='Слава')

    products = [
        Product(id='4811680006239', name='name 1', price=3.4, store=store0),
        Product(id='9785818319315', name='name 2', price=5.7, store=store1),
        Product(id='40822426', name='Водичка Славы', price=2.21, store=store0),
        Product(id='4810431004296', name='tim copybook 1', price=4.9, store=store0),
        Product(id='4690338982053', name='tim copybook 2', price=1.99, store=store1),
        Product(id='0000000000000', name='test 1', price=17.3, store=store1),
        Product(id='0000000000001', name='test 2', price=32.0, store=store0),
    ]

    db.session.add(store0)
    db.session.add(store1)

    db.session.add(user0)
    db.session.add(user1)

    for product in products:
        db.session.add(product)

    db.session.commit()
