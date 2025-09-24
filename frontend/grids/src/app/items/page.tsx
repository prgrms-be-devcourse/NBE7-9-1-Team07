import ItemCart from "../components/item-cart";
import MainHeader from "../components/main-header";

export default function MainPage() {
  return (
    <>
        <MainHeader />
        <div className="justify-center items-center m-4 h-full">
            <ItemCart />
        </div>
    </>
  );
}