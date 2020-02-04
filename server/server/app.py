from flask import Flask
from flask import request
from flask import jsonify
from flask import abort
from flask import render_template
from flask_sqlalchemy import SQLAlchemy
import os
from datetime import datetime


basedir = os.path.abspath(os.path.dirname(__file__))

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, 'data.db')

db = SQLAlchemy(app)


class Product(db.Model):
    id = db.Column(db.String(13), primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    price = db.Column(db.Float, nullable=False)

    store_id = db.Column(db.Integer, db.ForeignKey('store.id'), primary_key=True, nullable=False)

    def to_json(self):
        return jsonify({'id': self.id, 'name': self.name, 'price': self.price})

    @staticmethod
    def from_json(json):
        return Product(id=json['id'],
                       store_id=json['store_id'],
                       name=json['name'],
                       price=json['price'])


class Store(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    address = db.Column(db.String(300), nullable=False)

    products = db.relationship('Product', backref='store')


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100))

    requests = db.relationship('ProductRequest', backref='user')


class ProductRequest(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    time = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
    platform = db.Column(db.String(50))

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    product_id = db.Column(db.String(13), nullable=False)  # without ForeignKeyConstraint/relationship because product can be deleted.
    store_id = db.Column(db.Integer, nullable=False)  # -//-


@app.route('/')  # TODO remove
def index():
    return render_template('products_list.html', products=Product.query.all())


@app.route('/product', methods=['POST'])
def product_json():
    if request.is_json:
        content = request.get_json()

        product_id = content['product_id']
        store_id = content['store_id']
        user_id = content['user_id']

        product = get_product_or_404(product_id, store_id)
        add_product_request(user_id, product_id, store_id)
        return product.to_json()
    else:
        abort(404)


@app.route('/products/update', methods=['POST'])
def products_update():
    if request.is_json:
        content = request.get_json()
        results = []
        for product_json in content:
            product = Product.from_json(product_json)
            result = update_product(product, commit=False)
            results.append(result)
        db.session.commit()
        return jsonify(results)
    else:
        abort(404)


@app.route('/products/delete', methods=['POST'])
def products_delete():
    if request.is_json:
        content = request.get_json()
        results = []
        for product_json in content:
            id = product_json['id']
            store_id = product_json['store_id']
            is_deleted = delete_product(id, store_id, commit=False)
            results.append(is_deleted)
        db.session.commit()
        return jsonify(results)
    else:
        abort(404)


def get_product_or_404(product_id, store_id):
    error_message = "There is no product with code '{}' in store #{}.".format(product_id, store_id)
    product = Product.query.get_or_404((product_id, store_id), description=error_message)
    return product


def add_product_request(user_id, product_id, store_id, commit=True):

    error_message = "There is no user {}.".format(user_id)
    user = User.query.get_or_404(user_id, description=error_message)

    product_request = ProductRequest(user_id=user_id,
                                     product_id=product_id,
                                     store_id=store_id,
                                     platform=request.user_agent.platform)
    db.session.add(product_request)
    if commit:
        db.session.commit()


def update_product(product: Product, commit=True):
    result = None

    current_product = Product.query.get((product.id, product.store_id))

    if current_product:
        current_product.name = product.name
        current_product.price = product.price
        result = 'changed'
    elif Store.query.get(product.store_id) is not None:
        db.session.add(product)
        result = 'added'
    else:
        result = 'there is no store #{}.'.format(product.store_id)

    if commit:
        db.session.commit()

    return result


def delete_product(id, store_id, commit=True):
    product = Product.query.get((id, store_id))
    if product:
        db.session.delete(product)
        if commit:
            db.session.commit()
    return product is not None


if __name__ == '__main__':
    app.run()  # '0.0.0.0', 8000
